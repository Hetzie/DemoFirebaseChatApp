package com.demo.demofirebasechat.ui.chat.adapter


import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.base.BaseViewHolder
import com.demo.demofirebasechat.databinding.ItemChatReceiverBinding
import com.demo.demofirebasechat.databinding.ItemChatSenderBinding
import com.demo.demofirebasechat.extentions.toTimeFormat
import com.demo.demofirebasechat.extentions.visible
import com.demo.demofirebasechat.ui.chat.ChatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
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
    val today =
        Timestamp(Date(System.currentTimeMillis())).toTimeFormat("dd/MM/yyyy")
    var yesterday =
        Timestamp(Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24)))
            .toTimeFormat("dd/MM/yyyy")

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val mSenderBinding = senderBinding
        val mReceiverBinding = receiverBinding

        @RequiresApi(Build.VERSION_CODES.O)
        fun onBindType(position: Int, viewType: Int) {

            var date = false
            var createdTime =
                (messageList[position].get("createdAt") as Timestamp).toTimeFormat("dd/MM/yyyy")
            if (position == 0 || diffSameDate(position - 1, position)) {
                if (today == createdTime) {
                    createdTime = "Today"
                } else if (yesterday == createdTime) {
                    createdTime = "Yesterday"
                }
                date = true
            }
            if (viewType == SENDER_MESSAGE) {
                val status = (messageList[position].get("status") ?: 0).toString().toInt()

                if (status == 0) {
                    mSenderBinding.ivStatus.setImageResource(R.drawable.ic_sent)
                } else {
                    mSenderBinding.ivStatus.setImageResource(R.drawable.ic_delivered)
                }
                mSenderBinding.tvDate.visible(date)
                mSenderBinding.tvDate.text = createdTime
                mSenderBinding.tvMessage.text =
                    messageList[position].get("textMessage").toString()
                mSenderBinding.createdAtTime.text =
                    (messageList[position].get("createdAt") as Timestamp).toTimeFormat("hh:mm a")
            } else if (viewType == RECEIVER_MESSAGE) {
                mReceiverBinding.tvDate.visible(date)
                mReceiverBinding.tvDate.text = createdTime
                mReceiverBinding.tvMessage.text =
                    messageList[position].get("textMessage").toString()
                mReceiverBinding.createdAtTime.text =
                    (messageList[position].get("createdAt") as Timestamp).toTimeFormat("hh:mm a")
            }
        }

        override fun onBind(position: Int) {

        }
    }

    fun diffSameDate(position1: Int, position2: Int): Boolean {
        if ((messageList[position1].get("createdAt") as Timestamp).toTimeFormat("dd/MM/yyyy")
            == (messageList[position2].get("createdAt") as Timestamp).toTimeFormat("dd/MM/yyyy")
        ) {
            return false
        }
        return true
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
        this.ViewHolder(holder.itemView).onBindType(position, holder.itemViewType)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.setIsRecyclable(true)

    }
    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.setIsRecyclable(false)
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return messageList.size
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

