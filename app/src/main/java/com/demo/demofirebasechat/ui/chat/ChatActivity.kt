package com.demo.demofirebasechat.ui.chat

import android.os.Bundle
import androidx.lifecycle.asLiveData
import com.demo.demofirebasechat.BR
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.base.BaseActivity
import com.demo.demofirebasechat.databinding.ActivityChatBinding
import com.demo.demofirebasechat.ui.chat.adapter.ChatAdapter
import com.demo.demofirebasechat.ui.chat.adapter.QueryChatAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatBinding, ChatViewModel>() {

    override val layoutId: Int
        get() = R.layout.activity_chat
    override val bindingVariable: Int
        get() = BR.viewModel
    lateinit var chatAdapter: ChatAdapter
    lateinit var queryChatAdapter: QueryChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        myDataStore.getUserName.asLiveData().observe(this){
            mViewModel.senderUserName = it
        }
        queryChatAdapter = QueryChatAdapter(mViewModel.dbData, this, intent.getIntExtra("ChatPosition", 0))
        binding.tvDisplayName.text = intent.getStringExtra("ChatToDisplayName")!!
        mViewModel.receiverUserName = intent.getStringExtra("ChatToUsername")!!
        binding.rvChat.adapter = queryChatAdapter


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
        queryChatAdapter.startListening()
    }
}