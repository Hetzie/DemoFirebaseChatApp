package com.demo.demofirebasechat.ui.chat.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.base.BaseViewHolder
import com.demo.demofirebasechat.data.dummy.ChatModel
import com.demo.demofirebasechat.data.dummy.Message
import com.demo.demofirebasechat.data.dummy.UserProfile
import com.demo.demofirebasechat.databinding.ItemChatReceiverBinding
import com.demo.demofirebasechat.databinding.ItemChatSenderBinding
import com.demo.demofirebasechat.extentions.toTimeAmPm
import com.demo.demofirebasechat.ui.chat.ChatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.Date


open class ChatAdapter(
    var messageList: ArrayList<DocumentSnapshot>,
    var chatActivity: ChatActivity,
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    lateinit var senderBinding: ItemChatSenderBinding
    lateinit var receiverBinding: ItemChatReceiverBinding
    lateinit var context: Context
    private var registration: ListenerRegistration? = null
    var SENDER_MESSAGE = 0
    var RECEIVER_MESSAGE = 1


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val mSenderBinding = senderBinding
        val mReceiverBinding = receiverBinding

        @RequiresApi(Build.VERSION_CODES.O)
        fun onBindType(position: Int, viewType: Int) {

            Log.e("MESSAGE", messageList[position]["textMessage"].toString())

            if (viewType == SENDER_MESSAGE) {
                mSenderBinding.tvMessage.text = messageList[position].get("textMessage").toString()
                mSenderBinding.createdAtTime.text =
                    (messageList[position].get("createdAt") as Timestamp).toTimeAmPm("hh:mm a")
            } else {
                mReceiverBinding.tvMessage.text =
                    messageList[position].get("textMessage").toString()
                mReceiverBinding.createdAtTime.text =
                    (messageList[position].get("createdAt") as Timestamp).toTimeAmPm("hh:mm a")
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
            ViewHolder(senderBinding.root)
        } else {
            ViewHolder(receiverBinding.root)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        this.ViewHolder(holder.itemView).onBindType(position, holder.itemViewType)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].get("senderUsername")
                .toString() == chatActivity.mViewModel.senderUserName
        ) {
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

