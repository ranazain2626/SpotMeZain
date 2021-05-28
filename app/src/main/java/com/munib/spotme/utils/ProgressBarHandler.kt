package com.munib.spotme.utils

import android.content.Context
import android.widget.ProgressBar

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.munib.spotme.R


/**
 * Created by Mayur on 24-01-2018.
 */

class ProgressBarHandler(context: Context) : ProgressBar(context) {

    private var mProgressDialog: MaterialDialog? = null

    fun show(context: Context) {
        if (mProgressDialog != null && !mProgressDialog!!.isShowing) {
            mProgressDialog!!.show()
        } else {
            showProgressDialog(context)
        }
    }

    fun hide() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    fun showProgressDialog(context: Context) {
        val builder = MaterialDialog(context)
                .customView(R.layout.common_progress_dialog, null, false)
                .cancelable(false)
        mProgressDialog = builder
        if (!mProgressDialog!!.isShowing) {
            mProgressDialog!!.show()
        }
    }


}

