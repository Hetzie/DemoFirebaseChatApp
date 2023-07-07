package com.demo.demofirebasechat.fcmNotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.demo.demofirebasechat.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppOpenBroadCast : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {

        var intent = intent
        intent = Intent(context.applicationContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        /*if (intent.action.equals("123")){
            setInItData(context)
        }*/
        context.applicationContext.startActivity(intent)
        context.unregisterReceiver(this)
        Log.e("onStop()", " PowerButton")
    }

}