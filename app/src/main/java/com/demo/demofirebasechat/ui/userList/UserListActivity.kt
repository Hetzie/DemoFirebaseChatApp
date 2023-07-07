package com.demo.demofirebasechat.ui.userList

import android.os.Bundle
import androidx.lifecycle.asLiveData
import com.demo.demofirebasechat.BR
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.base.BaseActivity
import com.demo.demofirebasechat.databinding.ActivityUserListBinding
import com.demo.demofirebasechat.extensions.showToast
import com.demo.demofirebasechat.ui.userList.adapter.UserAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserListActivity : BaseActivity<ActivityUserListBinding, UserListViewModel>() {

    override val layoutId: Int
        get() = R.layout.activity_user_list
    override val bindingVariable: Int
        get() = BR.viewModel
    lateinit var userAdapter: UserAdapter
    val db = FirebaseFirestore.getInstance()
    val dbData = db.collection("Users")
    var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userAdapter = UserAdapter(dbData, this)
        binding.rvMain.adapter = userAdapter

        myDataStore.getUserName.asLiveData().observe(this) {
            userName = it
            userAdapter.notifyDataSetChanged()
            FirebaseMessaging.getInstance().subscribeToTopic(it)
                .addOnCompleteListener { task ->
                    var msg = "Subscribed to Notification"
                    if (!task.isSuccessful) {
                        msg = "Subscribe failed"
                    }
                    showToast(msg)
                }
        }

        myDataStore.getDisplayName.asLiveData().observe(this) {
            binding.tvDisplayName.text =
                getString(R.string.hii_display_name_format, it)
        }
        onClicks()
    }

    private fun onClicks() {
        binding.apply {
            btnLogOut.setOnClickListener {
                mViewModel.logOut()
            }
        }
    }

    override fun setupObservable() {

    }

    override fun onStart() {
        super.onStart()
        userAdapter.startListening()
    }
}