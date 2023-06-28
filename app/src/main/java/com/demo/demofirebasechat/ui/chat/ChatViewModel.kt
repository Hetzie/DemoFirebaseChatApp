package com.demo.demofirebasechat.ui.chat

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.demofirebasechat.base.BaseViewModel
import com.demo.demofirebasechat.data.dummy.ChatModel
import com.demo.demofirebasechat.data.dummy.Message
import com.demo.demofirebasechat.datastore.MyDataStore
import com.demo.demofirebasechat.extentions.showToast
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    var context: Context,
    var myDataStore: MyDataStore,
) : BaseViewModel<Any>() {
    var receiverDisplayName = ""
    var senderUserName = ""
    var receiverUserName = ""
    var textMessage = MutableLiveData("")
    val dbData = FirebaseFirestore.getInstance().collection("Chats")
    val message = Message(textMessage = textMessage.value!!)


    fun createChat() {
        val chat = ChatModel(senderUserName, receiverUserName, messageList = mutableListOf())
        dbData.document(chat.ChatId).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (!document.exists()) {
                        dbData.document(chat.ChatId).set(chat)
                            .addOnSuccessListener {
                                context.showToast("Chat created!")
                            }
                            .addOnFailureListener { e ->
                                context.showToast(e.message.toString())
                            }
                    }

                }
            }
    }

    fun sendMessage(textMessage: String) {
        val chat = ChatModel(senderUserName, receiverUserName, messageList = mutableListOf())

        dbData.document(chat.ChatId).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document.exists()) {
                        dbData.document(chat.ChatId)
                            .update(
                                "messageList",
                                FieldValue.arrayUnion(Message(textMessage = textMessage))
                            )
                            .addOnSuccessListener { it ->
                                context.showToast("done!!")
                                viewModelScope.launch {

                                }

                            }.addOnFailureListener {
                                context.showToast("error!!")
                            }

                    } else {
                       /* dbData.document(chat.ChatId).set(chat)
                            .addOnSuccessListener {
                                context.showToast("data inserted.")

                            }
                            .addOnFailureListener { e ->
                                context.showToast(e.message.toString())
                            }*/
                    }

                    /*dbData.get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document.exists()) {
                        dbData.
                    } else {
                        dbData.set(chat)
                            .addOnSuccessListener {
                                viewModelScope.launch {

                                }
                            }
                            .addOnFailureListener { e ->
                                context.showToast(e.message.toString())
                            }
                    }
                }
            }*/
                }
            }
    }
}