package com.demo.demofirebasechat.ui.chat.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.base.BaseViewHolder
import com.demo.demofirebasechat.data.dummy.ChatModel
import com.demo.demofirebasechat.data.dummy.Message
import com.demo.demofirebasechat.data.dummy.UserProfile
import com.demo.demofirebasechat.databinding.ItemChatReceiverBinding
import com.demo.demofirebasechat.databinding.ItemChatSenderBinding
import com.demo.demofirebasechat.ui.chat.ChatActivity
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot


open class ChatAdapter(
    val messageList: ArrayList<Message>
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>(){

    lateinit var senderBinding: ItemChatSenderBinding
    lateinit var receiverBinding: ItemChatReceiverBinding
    lateinit var context: Context
    private val snapshot = ArrayList<DocumentSnapshot>()
    private var registration: ListenerRegistration? = null
    var SENDER_MESSAGE = 0
    var RECEIVER_MESSAGE = 1


    inner class ViewHolder(itemView: View, viewType: Int) : BaseViewHolder(itemView) {
        val mSenderBinding = senderBinding
        val mReceiverBinding = receiverBinding



        override fun onBind(position: Int) {
            if (itemViewType == SENDER_MESSAGE) {
                mSenderBinding.tvMessage.text = messageList[position].textMessage
            }else{
                mReceiverBinding.tvMessage.text = messageList[position].textMessage
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        senderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_chat_sender,
            parent,
            false
        )
        receiverBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_chat_receiver,
            parent,
            false
        )
        return if (viewType == SENDER_MESSAGE) {
            ViewHolder(senderBinding.root, SENDER_MESSAGE)
        } else {
            ViewHolder(receiverBinding.root, RECEIVER_MESSAGE)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        this.ViewHolder(holder.itemView, holder.itemViewType).onBind(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            SENDER_MESSAGE
        } else {
            RECEIVER_MESSAGE
        }
    }

}

