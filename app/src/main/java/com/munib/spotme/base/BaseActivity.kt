package com.munib.spotme.base


import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
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
import java.util.*


abstract class BaseActivity : AppCompatActivity(),BaseView {
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey = "key=" + "AAAAaRmFqg4:APA91bGGUM14NFLhCSleYZ7EwqCteCL46NbfJ-i8OcE6GXfu-fM5-s5IUdmux8lRolqrkwTSV8jfH1oPaUHLgedjF4npJEMhRSIXnrUnqZ9LvoDPhrTHvSIHx69r5Q_DrBMaumuLD0zR"
    private val contentType = "application/json"

    private var progressBarHanlder: ProgressBarHandler? = null
    lateinit var mPref: PreferenceHelper
    lateinit var database :FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var currentUserData: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPref = PreferenceHelper(this)
        database = FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        Log.d("current_user", "asd")
        val ref = FirebaseDatabase.getInstance().reference.child("users")
        if(auth.currentUser!=null) {

            ref.child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        currentUserData = dataSnapshot.getValue(UserModel::class.java)!!
                        currentUserData.uid = dataSnapshot.key

                        ref.child(auth.currentUser!!.uid).child("customer").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    currentUserData.customer_id = dataSnapshot.child("id").getValue().toString()
                                    Log.d("customer-Id",currentUserData.customer_id)
//                        Log.d("current_user", currentUserData.email)
                                } else {
                                    //                      Log.d("current_user", "currentUser.email")
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d(CommonUtils.TAGZ, error.details)
                            }
                        })
//                        Log.d("current_user", currentUserData.email)
                    } else {
  //                      Log.d("current_user", "currentUser.email")
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
            progressBarHanlder = ProgressBarHandler(this)
        }
        progressBarHanlder!!.show(this)
    }

    override fun hideProgress() {
        if (progressBarHanlder != null) {
            progressBarHanlder?.hide()
        }
    }

    override fun showMessage(message: String) {
        /* Alerter.create(this)
                 .setTitle("Alert")
                 .setText(message)
                 .setDuration(2000)
                 .show()*/

/*        val customdialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        val window = customdialog.getWindow()
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)*/

        val dialog = MaterialDialog(this)
                .customView(R.layout.dialog_msg, null, true)
        dialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this, android.R.color.transparent))
        val view = dialog.getCustomView()

        val edtMsg = view?.findViewById(R.id.txtMedicationName) as AppCompatTextView

        edtMsg.text = message

        val txtUpdate = view?.findViewById(R.id.txtUpdate) as AppCompatTextView

        txtUpdate.setOnClickListener {

            dialog.dismiss()
        }

        /*  val imgClose = view.findViewById(R.id.img_remove_list_item) as AppCompatImageView
          imgClose.setOnClickListener {
              CommonUtils.hideSoftKeyboard(this)
              dialog.dismiss()
          }*/

        if (!dialog.isShowing) {
            dialog.show()
        }
    }


    @ColorInt
    public fun getColorRes(res: Int): Int {
        return ContextCompat.getColor(this, res)
    }

    @SuppressLint("SupportAnnotationUsage")
    @DrawableRes
    public fun getDrawableRes(res: Int): Drawable? {
        return ContextCompat.getDrawable(this, res)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        CommonUtils.hideSoftKeyboard(this)
    }

    public fun showToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun hideEmptyView() {

    }

    fun sendNotification(notification_st: String, to: String) {


        val notification = JSONObject()
        val notifcationBody = JSONObject()
        val notifcationBody1 = JSONObject()

        try {
            notifcationBody.put("title", "SpotMe")
            notifcationBody.put("message", notification_st) //Enter your notification message
            notifcationBody1.put("title", notification_st)
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
        })
        {
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap();
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        Volley.newRequestQueue(BaseActivity@this).add(jsonRequest)
    }
}