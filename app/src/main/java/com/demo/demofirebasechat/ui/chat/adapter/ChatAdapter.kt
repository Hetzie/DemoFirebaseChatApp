package com.demo.demofirebasechat.ui.chat.adapter


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
import com.demo.demofirebasechat.databinding.ItemChatReceiverBinding
import com.demo.demofirebasechat.databinding.ItemChatSenderBinding
import com.demo.demofirebasechat.extensions.toTimeFormat
import com.demo.demofirebasechat.extensions.visible
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
    val today = Timestamp(Date(System.currentTimeMillis())).toTimeFormat("dd/MM/yyyy")
    var yesterday = Timestamp(Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24)))
        .toTimeFormat("dd/MM/yyyy")

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val mSenderBinding = senderBinding
        val mReceiverBinding = receiverBinding

        @RequiresApi(Build.VERSION_CODES.O)
        fun onBindType(position: Int, viewType: Int) {
            val date = setDate(position)
            val msg = messageList[position].get("textMessage").toString()
            val createdTime =
                (messageList[position].get("createdAt") as Timestamp).toTimeFormat("hh:mm a")
            val status = (messageList[position].get("status") ?: 0).toString().toInt()
            Log.e(
                "POSITION->$position",
                "STATUS->" + (messageList[position].get("status").toString())
            )

            if (viewType == SENDER_MESSAGE) {
                mSenderBinding.tvDate.visible(date.first)
                mSenderBinding.tvDate.text = date.second
                mSenderBinding.tvMessage.text = msg
                mSenderBinding.createdAtTime.text = createdTime
                when (status) {
                    0 -> {
                        mSenderBinding.ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_clock))
                    }

                    1 -> {
                        mSenderBinding.ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_sent))
                    }

                    2 -> {
                        mSenderBinding.ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_delivered))
                    }

                    3 -> {
                        mSenderBinding.ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_delivered))
                        mSenderBinding.ivStatus.setColorFilter(R.color.black)
                    }

                    else -> {
                        mSenderBinding.ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_error))
                    }
                }
            } else {

                mReceiverBinding.tvDate.visible(date.first)
                mReceiverBinding.tvDate.text = date.second
                mReceiverBinding.tvMessage.text = msg
                mReceiverBinding.createdAtTime.text = createdTime
            }
        }

        override fun onBind(position: Int) {

        }
    }

    fun setDate(position: Int): Pair<Boolean, String> {
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
        return Pair(date, createdTime)
    }

    private fun diffSameDate(position1: Int, position2: Int): Boolean {
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
        this.ViewHolder(holder.itemView)
            .onBindType(position, holder.itemViewType)
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

