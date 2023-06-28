package com.demo.demofirebasechat.ui.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.base.BaseViewHolder
import com.demo.demofirebasechat.data.dummy.ChatModel
import com.demo.demofirebasechat.data.dummy.UserProfile
import com.demo.demofirebasechat.databinding.ItemChatSenderBinding
import com.demo.demofirebasechat.extentions.startNewActivity
import com.demo.demofirebasechat.ui.chat.ChatActivity
import com.demo.demofirebasechat.ui.userList.UserListActivity
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.text.FieldPosition

open class QueryChatAdapter(
    var query: Query,
    var activity: ChatActivity,
    var chatPosition: Int,
) : RecyclerView.Adapter<QueryChatAdapter.ViewHolder>(), EventListener<QuerySnapshot> {

    lateinit var binding: ItemChatSenderBinding
    lateinit var context: Context
    private val snapshot = ArrayList<DocumentSnapshot>()
    private var registration: ListenerRegistration? = null



    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val mBinding = binding

        override fun onBind(position: Int) {
            mBinding.tvMessage.text = getItem(chatPosition).messageList[position].textMessage
            Log.e("MESSAGE", getItem(chatPosition).messageList[position].textMessage)
            if (position == getItem(chatPosition).messageList.lastIndex){
                activity.binding.rvChat.scrollToPosition(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_chat_sender,
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        this.ViewHolder(holder.itemView).onBind(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getItem(index: Int): ChatModel {
        var chat: ChatModel
        snapshot[chatPosition].let { snapshot ->
            chat = snapshot.toObject(ChatModel::class.java)!!
            Log.e("CHAT", chat.messageList.toString())
        }
        return chat
    }

    fun startListening() {
        if (registration == null) {
            registration = query.addSnapshotListener(this)
        }
    }

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.e("onEvent:error", error.toString())
            return
        }

        for (change in value!!.documentChanges) {
            when (change.type) {
                DocumentChange.Type.ADDED -> onDocumentAdded(change)
                DocumentChange.Type.REMOVED -> onDocumentRemoved(change)
                DocumentChange.Type.MODIFIED -> onDocumentModified(change)
            }
        }
    }

    protected open fun onDocumentAdded(change: DocumentChange) {
        snapshot.add(change.newIndex, change.document)
        notifyDataSetChanged()
    }

    protected open fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex == change.newIndex) {
            snapshot[change.newIndex] = change.document
            notifyDataSetChanged()
        } else {
            snapshot.removeAt(change.oldIndex)
            snapshot.add(change.newIndex, change.document)
            notifyDataSetChanged()
        }
    }

    protected open fun onDocumentRemoved(change: DocumentChange) {
        snapshot.removeAt(change.oldIndex)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (snapshot.size!= 0) {
            snapshot[chatPosition].toObject(ChatModel::class.java)?.messageList?.size ?: 0
        } else{
            0
        }


    }
}