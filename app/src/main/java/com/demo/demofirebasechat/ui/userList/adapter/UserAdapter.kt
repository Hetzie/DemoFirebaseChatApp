package com.demo.demofirebasechat.ui.userList.adapter

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
import com.demo.demofirebasechat.databinding.ItemUserBinding
import com.demo.demofirebasechat.extentions.showToast
import com.demo.demofirebasechat.extentions.startNewActivity
import com.demo.demofirebasechat.ui.chat.ChatActivity
import com.demo.demofirebasechat.ui.userList.UserListActivity
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

open class UserAdapter(
    var query: Query,
    var activity: UserListActivity,
) : RecyclerView.Adapter<UserAdapter.ViewHolder>(), EventListener<QuerySnapshot> {

    lateinit var binding: ItemUserBinding
    lateinit var context: Context
    private val snapshot = ArrayList<DocumentSnapshot>()
    private var registration: ListenerRegistration? = null
    val dbData = FirebaseFirestore.getInstance().collection("Chats")

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val mBinding = binding

        override fun onBind(position: Int) {
            mBinding.model = getItem(position)
            val bundle = Bundle()
            bundle.putString("ChatToUsername", getItem(position).userName)
            bundle.putString("ChatToDisplayName", getItem(position).displayName)
            bundle.putInt("ChatPosition", position)
            mBinding.clChat.setOnClickListener {
                createChat(activity.userName ?: "", getItem(position).userName, bundle)
            }
        }
    }

    fun createChat(senderUserName: String, receiverUserName: String, bundle: Bundle) {

        val docId = listOf(senderUserName, receiverUserName).sorted()

        Log.e("DOC_ID", docId.toString())

        var chatId = "Chat:${docId[0]},${docId[1]}"
        var chat =  ChatModel(senderUserName, receiverUserName, chatId = chatId)
        bundle.putString("ChatId", chatId)

        dbData.document(chatId).get()
            .addOnCompleteListener {
                try {
                val document = it.result
                if (document.exists()){
                    Log.e("DocumentSnap", "Exist")
                    activity.startNewActivity(ChatActivity::class.java, bundle = bundle)
                }else {
                    dbData.document(chatId).collection("messageList")
                    activity.startNewActivity(ChatActivity::class.java, bundle = bundle)
                }

                } catch (e: Exception) {
                    context.showToast(e.message.toString().substringAfter(":"))
                }

            }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_user,
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

    fun getItem(index: Int): UserProfile {
        var user: UserProfile
        snapshot[index].let { snapshot ->
            user = snapshot.toObject(UserProfile::class.java) ?: UserProfile()
        }
        return user
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
        notifyItemInserted(change.newIndex)
    }

    protected open fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex == change.newIndex) {
            snapshot[change.oldIndex] = change.document
            notifyItemChanged(change.oldIndex)
        } else {
            snapshot.removeAt(change.oldIndex)
            snapshot.add(change.newIndex, change.document)
            notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    protected open fun onDocumentRemoved(change: DocumentChange) {
        snapshot.removeAt(change.oldIndex)
        notifyItemRemoved(change.oldIndex)
    }

    override fun getItemCount(): Int {
        return snapshot.size
    }
}