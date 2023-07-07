package com.demo.demofirebasechat.ui.chat

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.demofirebasechat.RetrofitInstance
import com.demo.demofirebasechat.base.BaseViewModel
import com.demo.demofirebasechat.data.dummy.ChatModel
import com.demo.demofirebasechat.data.dummy.Message
import com.demo.demofirebasechat.data.dummy.NotificationData
import com.demo.demofirebasechat.data.dummy.PushNotification
import com.demo.demofirebasechat.datastore.MyDataStore
import com.demo.demofirebasechat.extensions.showToast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    var context: Context,
    var myDataStore: MyDataStore,
) : BaseViewModel<Any>() {
    var receiverDisplayName = ""
    var senderUserName = ""
    var receiverUserName = ""
    var chatId = ""
    var textMessage = MutableLiveData("")
    val dbChatData = FirebaseFirestore.getInstance().collection("Chats")
    val dbUserData = FirebaseFirestore.getInstance().collection("Users")
    val message = Message(textMessage = textMessage.value!!)

    var statusUpdate : MutableLiveData<Int> = MutableLiveData()
    var observerStatusUpdate : LiveData<Int> = statusUpdate

    fun sendMessage(textMessage: String, lifecycleOwner: LifecycleOwner) {
        val chat =
            ChatModel(senderUsername = senderUserName, receiverUserName = receiverUserName, chatId = chatId, textMessage = textMessage)


        dbChatData.document(chatId).collection("messageList").add(chat)
            .addOnSuccessListener {
                //message deliver update
                observerStatusUpdate.observe(lifecycleOwner){ a ->
                    it.update("status", a)
                }

                it.update("docId", it.id).addOnCompleteListener { a ->
                    chat.docId = it.id
                    statusUpdate.postValue(1)
                    val topic = "/topics/$receiverUserName"

                    PushNotification(
                        chat,
                        topic).also {
                        sendNotification(it)
                    }
                }


            }.addOnFailureListener {
                context.showToast("error!!")
            }

    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {

            } else {
                Log.e("TAG", response.errorBody()!!.string())
            }
        } catch(e: Exception) {
            Log.e("TAG", e.toString())
        }
    }
}