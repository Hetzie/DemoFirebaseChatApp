package com.demo.demofirebasechat.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat


class Extensions{
    companion object{
//        var FCM_TOKEN =  MutableLiveData<String>()
        const val BASE_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = "AAAAB3YLgvo:APA91bFM2IOSCdGoNz0z12C_tZKyn42w5It1FLe_aNl0091axwctR8w3ZueRO42B5u_-aflrkILnEvhRSZo_T3cUuEJRfk_40afo3rylyH9OHSLN98Ef3VhZyfI-on6-gDygE95s-q7L" // get firebase server key from firebase project setting
        const val CONTENT_TYPE = "application/json"
    }
}
fun <T> Activity.startNewActivity(
    className: Class<T>,
    finish: Boolean = false,
    bundle: Bundle? = null,
    clearTask: Boolean = false,
) {
    hideKeyboard()
    val intent = Intent(this, className)
    bundle?.let {
        intent.putExtras(it)
    }

    if (clearTask){
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    startActivity(intent)
    if (finish) {
        finish()
    }
}

fun Context.hideKeyboard() {
    this as Activity
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

@SuppressLint("SimpleDateFormat")
fun Timestamp.toTimeFormat(pattern: String?=null): String {
    val sfd = SimpleDateFormat(pattern?:"dd-MM-yyyy HH:mm:ss")
    return sfd.format(this.toDate())
}

fun View.visible(value: Boolean = true) {
    visibility = if (value) {
        View.VISIBLE
    }else{
        View.GONE
    }
}