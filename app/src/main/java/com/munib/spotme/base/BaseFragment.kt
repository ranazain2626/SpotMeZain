package com.munib.spotme.base

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatTextView
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.munib.spotme.R
import com.munib.spotme.dataModels.UserModel
import com.munib.spotme.utils.CommonUtils
import com.munib.spotme.utils.PreferenceHelper
import com.munib.spotme.utils.ProgressBarHandler
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by user on 19/5/18.
 */
abstract class BaseFragment : androidx.fragment.app.Fragment(), BaseView {
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey = "key=" + "AAAAEOEuJes:APA91bE82sbkhHml3ntSmFFVjb2VDXgWc9Fk44FIpb6iDI_PxxzyOZRqusVfZSu1IWU-Co6rIYnT1a-HG26HRTWaLUsskOoGeBn5keeA11gUt-iPVlCn2fTaSQSYb2v9Fq2AQX0ZVydj"
    private val contentType = "application/json"

    private val CAMERA_MIC_PERMISSION_REQUEST_CODE = 1000
    private var progressBarHanlder: ProgressBarHandler? = null
    lateinit var mPref: PreferenceHelper
    lateinit var auth:FirebaseAuth
    lateinit var currentUserData: UserModel
    lateinit var database :FirebaseDatabase

    protected abstract fun injectView()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPref = PreferenceHelper(activity)
        database = FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        val ref = FirebaseDatabase.getInstance().reference.child("users")
        if(auth.currentUser!=null) {

            ref.child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        currentUserData = dataSnapshot.getValue(UserModel::class.java)!!
                        currentUserData.uid=dataSnapshot.key
                        Log.d("current_user", currentUserData.email)
                    } else {
                        Log.d("current_user", "currentUser.email")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(CommonUtils.TAGZ, error.details)
                }
            })
        }
    }


    override fun showProgress() {
        if (progressBarHanlder == null) {
            progressBarHanlder = activity?.let { ProgressBarHandler(it) }
        }
        activity?.let { progressBarHanlder!!.show(it) }
    }

    override fun hideProgress() {
        if (progressBarHanlder != null) {
            progressBarHanlder?.hide()
        }
    }

    override fun showMessage(message: String) {
        /* Alerter.create(activity)
                 .setTitle("Alert")
                 .setText(message)
                 .setDuration(2000)
                 .show()*/

        val dialog = activity?.let {
            MaterialDialog(it)
                .customView(R.layout.dialog_msg, null, true)
        }
        dialog!!.window!!.setBackgroundDrawable(activity?.let { ContextCompat.getDrawable(it, android.R.color.transparent) })
        val view = dialog.getCustomView()

        val edtMsg = view?.findViewById(R.id.txtMedicationName) as AppCompatTextView

        edtMsg.text = message

        val txtUpdate = view?.findViewById(R.id.txtUpdate) as AppCompatTextView

        txtUpdate.setOnClickListener {

            dialog.dismiss()
        }
/*
        val imgClose = view.findViewById(R.id.img_remove_list_item) as AppCompatImageView
        imgClose.setOnClickListener {
            CommonUtils.hideSoftKeyboard(activity)
            dialog.dismiss()
        }*/

        if (!dialog.isShowing) {
            dialog.show()
        }

    }

    override fun hideEmptyView() {

    }

    @ColorInt
    public fun getColorRes(res: Int): Int? {
        return activity?.let { ContextCompat.getColor(it, res) }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

    }


    @SuppressLint("SupportAnnotationUsage")
    @DrawableRes
    public fun getDrawableRes(res: Int): Drawable? {
        return activity?.let { ContextCompat.getDrawable(it, res) }
    }


    public fun showToast(msg: String?) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
    override fun onResume() {
        super.onResume()
    }

    fun sendNotification(notification: String, to: String) {


        val notification = JSONObject()
        val notifcationBody = JSONObject()
        val notifcationBody1 = JSONObject()

        try {
            notifcationBody.put("title", "SpotMe")
            notifcationBody.put("message", notification) //Enter your notification message
            notifcationBody1.put("title", notification)
            notifcationBody1.put("message", "SpotMe")
            notifcationBody1.put("sound", "default")
            notification.put("to", to)
            notification.put("data", notifcationBody)
            notification.put("notification", notifcationBody1)
            Log.e("TAG", "try")
        } catch (exception: JSONException) {
            exception.printStackTrace()
        }
        Log.e("TAG", "sendNotification")
        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, FCM_API, notification, Response.Listener {
            Log.d("mubi",it.toString())
        }, Response.ErrorListener { error ->
            error.printStackTrace()

        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap();
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        Volley.newRequestQueue(getActivity()).add(jsonRequest)
    }
}

