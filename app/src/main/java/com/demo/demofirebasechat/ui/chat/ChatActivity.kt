package com.demo.demofirebasechat.ui.chat

import android.os.Bundle
import androidx.lifecycle.asLiveData
import com.demo.demofirebasechat.BR
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.base.BaseActivity
import com.demo.demofirebasechat.databinding.ActivityChatBinding
import com.demo.demofirebasechat.ui.chat.adapter.ChatAdapter
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

    private lateinit var firestore: FirebaseFirestore
    private lateinit var listener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setData()
        chatRvInit()
        setUpChatsDocumentListener()
        onClick()
    }

    private fun onClick() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnSendMessage.setOnClickListener {
            if (binding.etMessage.text.trim().isNotEmpty()) {
                mViewModel.sendMessage(
                    binding.etMessage.text.toString(),
                    this
                )
                binding.etMessage.setText("")
            }
        }
    }

    override fun setupObservable() {

    }

    private fun setData(){
        myDataStore.getUserName.asLiveData().observe(this) {
            mViewModel.senderUserName = it
        }
        binding.tvDisplayName.text = intent.getStringExtra("ChatToDisplayName")!!
        mViewModel.receiverUserName = intent.getStringExtra("ChatToUsername")!!
        mViewModel.chatId = intent.getStringExtra("ChatId")!!
    }

    private fun chatRvInit(){
        // Initialize RecyclerView and adapter
        chatAdapter = ChatAdapter(arrayListOf(), this)
        binding.rvChat.adapter = chatAdapter
    }
    private fun setUpChatsDocumentListener(){
        // Set up listener for real-time updates
        firestore = FirebaseFirestore.getInstance()
        val documentRef = firestore.collection("Chats").document(mViewModel.chatId)
        listener = documentRef.collection("messageList")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    for (change in snapshot.documentChanges) {
                        when (change.type) {
                            DocumentChange.Type.ADDED -> chatAdapter.onDocumentAdded(change)
                            DocumentChange.Type.REMOVED -> chatAdapter.onDocumentRemoved(change)
                            DocumentChange.Type.MODIFIED -> chatAdapter.onDocumentModified(change)
                        }
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listener.remove()
    }
}