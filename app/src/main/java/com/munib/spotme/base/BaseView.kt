package com.munib.spotme.base


interface BaseView {
    fun showProgress()
    fun hideProgress()
    fun hideEmptyView()
    fun showMessage(message: String)
    interface Presenter {
        fun callLogWS(action:String,message:String)
    }
}