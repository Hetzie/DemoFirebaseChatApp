package com.demo.demofirebasechat.ui.chat

import android.os.Bundle
import androidx.lifecycle.asLiveData
import com.demo.demofirebasechat.BR
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.base.BaseActivity
import com.demo.demofirebasechat.databinding.ActivityChatBinding
import com.demo.demofirebasechat.ui.chat.adapter.ChatAdapter
import com.demo.demofirebasechat.ui.chat.adapter.QueryChatAdapter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatBinding, ChatViewModel>() {

    override val layoutId: Int
        get() = R.layout.activity_chat
    override val bindingVariable: Int
        get() = BR.viewModel
    lateinit var chatAdapter: ChatAdapter
    lateinit var queryChatAdapter: QueryChatAdapter

    private lateinit var firestore: FirebaseFirestore
    private lateinit var listener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        myDataStore.getUserName.asLiveData().observe(this) {
            mViewModel.senderUserName = it
        }
        binding.tvDisplayName.text = intent.getStringExtra("ChatToDisplayName")!!
        mViewModel.receiverUserName = intent.getStringExtra("ChatToUsername")!!
        mViewModel.chatId = intent.getStringExtra("ChatId")!!

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize RecyclerView and adapter
        chatAdapter = ChatAdapter(arrayListOf(), this)
        binding.rvChat.adapter = chatAdapter

        // Set up listener for real-time updates
        val documentRef = firestore.collection("Chats").document(mViewModel.chatId)
        listener = documentRef.collection("messageList")
//            .whereNotEqualTo("textMessage", "")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle the exception
                    return@addSnapshotListener
                }

                if (snapshot != null) {
//                    val items = snapshot.documents
//                    chatAdapter.updateData(items)

                    for (change in snapshot.documentChanges) {
                        when (change.type) {
                            DocumentChange.Type.ADDED -> chatAdapter.onDocumentAdded(change)
                            DocumentChange.Type.REMOVED -> chatAdapter.onDocumentRemoved(change)
                            DocumentChange.Type.MODIFIED -> chatAdapter.onDocumentModified(change)
                        }
                    }
                }
            }



//        queryChatAdapter =
//            QueryChatAdapter(mViewModel.dbData, this, intent.getIntExtra("ChatPosition", 0))
//        binding.rvChat.adapter = queryChatAdapter

        onClick()
    }

    fun onClick() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnSendMessage.setOnClickListener {
            if (binding.etMessage.text.trim().isNotEmpty()) {
                mViewModel.sendMessage(
                    binding.etMessage.text.toString()
                )
                binding.etMessage.setText("")
            }
        }
    }

    override fun setupObservable() {

    }


    override fun onStart() {
        super.onStart()
//        queryChatAdapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the snapshot listener when the activity is destroyed
        listener.remove()
    }
}