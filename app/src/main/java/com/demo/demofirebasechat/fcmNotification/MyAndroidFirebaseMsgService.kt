package com.demo.demofirebasechat.fcmNotification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.data.dummy.ChatModel
import com.demo.demofirebasechat.extensions.Extensions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random


lateinit var notificationManager: NotificationManager
lateinit var notificationChannel: NotificationChannel
lateinit var builder: Notification.Builder
private val channelId = "venditoLive"
private val description = "Vendito notification"
private var intent: Intent? = null
var notificationId = 0
var updateActivity = false

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {


    @Inject
    lateinit var context: Context

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("newToken", s)
//        Extensions.FCM_TOKEN.postValue(s)
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("onMessageReceived", remoteMessage.data.toString())
        val data = remoteMessage.data
        var chatId = ""
        var docId = ""
        var title = ""
//        var extraParam: Any = ""
        if (data.containsKey("chatId")) {
            chatId = data["chatId"].toString()
        }
        if (data.containsKey("docId")) {
            docId = data["docId"].toString()
        }

        val dbChatData = FirebaseFirestore.getInstance().collection("Chats")
        dbChatData.document(chatId).collection("messageList").document(docId).update("status", 2)



        /*if (data.containsKey("extraParam")) {
            extraParam = data["extraParam"]!!
        }*/
        notificationId = Random.nextInt()

//        showNotification(title, message)

    }

    private fun showNotification(title: String?, body: String?) {
        /*val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }*/
        val pendingIntent: PendingIntent? = if (!updateActivity) {
            PendingIntent.getActivity(
                this,
                (Math.random() * 100).toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            updateActivity = false
            null
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
            if (pendingIntent == null) {
                builder = Notification.Builder(this, channelId)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(Notification.BigTextStyle().bigText(body))
                    .setBadgeIconType(R.mipmap.ic_launcher)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                val notId = notificationId
                notificationManager.notify(notId, builder.build())
                val h = Handler(Looper.getMainLooper())
                val delayInMilliseconds: Long = 2000
                h.postDelayed(
                    { notificationManager.cancel(notId) },
                    delayInMilliseconds
                )

            } else {
                builder = Notification.Builder(this, channelId)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(Notification.BigTextStyle().bigText(body))
                    .setBadgeIconType(R.mipmap.ic_launcher)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setFullScreenIntent(pendingIntent, true)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                notificationManager.notify(notificationId, builder.build())

            }

        }
    }


    fun updateMyActivity(
        context: Context,
        message: String?,
        orderId: String = "",
        productId: String = "",
        slug: String = ""
    ) {
        updateActivity = true
        val intent = Intent("updateMyActivity")
        intent.putExtra("message", message)
        if (message == "updateLiveActivity") {
            updateActivity = false
        }
        context.sendBroadcast(intent)
    }

}