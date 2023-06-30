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
    var messageList: ArrayList<DocumentSnapshot>,
    var chatActivity: ChatActivity
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>(){

    lateinit var senderBinding: ItemChatSenderBinding
    lateinit var receiverBinding: ItemChatReceiverBinding
    lateinit var context: Context
    private var registration: ListenerRegistration? = null
    var SENDER_MESSAGE = 0
    var RECEIVER_MESSAGE = 1


    inner class ViewHolder(itemView: View, viewType: Int) : BaseViewHolder(itemView) {
        val mSenderBinding = senderBinding
        val mReceiverBinding = receiverBinding

        fun onBindType(position: Int, viewType: Int) {

            Log.e("MESSAGE", messageList[position]["textMessage"].toString())

            if (viewType == SENDER_MESSAGE) {
                mSenderBinding.tvMessage.text = messageList[position].get("textMessage").toString()
            }else{
                mReceiverBinding.tvMessage.text = messageList[position].get("textMessage").toString()
            }
        }

        override fun onBind(position: Int) {

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        this.ViewHolder(holder.itemView, holder.itemViewType).onBindType(position, holder.itemViewType)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].get("senderUsername").toString() == chatActivity.mViewModel.senderUserName) {
            SENDER_MESSAGE
        } else {
            RECEIVER_MESSAGE
        }
    }
    fun updateData(newItems: ArrayList<DocumentSnapshot>) {
        messageList = newItems
        notifyDataSetChanged()
    }

    open fun onDocumentAdded(change: DocumentChange) {
        messageList.add(change.newIndex, change.document)
        notifyItemInserted(change.newIndex)
        chatActivity.binding.rvChat.scrollToPosition(messageList.lastIndex)
    }

    open fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex == change.newIndex) {
            messageList[change.oldIndex] = change.document
            notifyItemChanged(change.oldIndex)
        } else {
            messageList.removeAt(change.oldIndex)
            messageList.add(change.newIndex, change.document)
            notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    open fun onDocumentRemoved(change: DocumentChange) {
        messageList.removeAt(change.oldIndex)
        notifyItemRemoved(change.oldIndex)
    }
}

