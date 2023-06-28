package com.demo.demofirebasechat.ui.userList

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.asLiveData
import com.demo.demofirebasechat.BR
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.base.BaseActivity
import com.demo.demofirebasechat.data.dummy.UserProfile
import com.demo.demofirebasechat.databinding.ActivityUserListBinding
import com.demo.demofirebasechat.datastore.MyDataStore.Companion.dataStore
import com.demo.demofirebasechat.extentions.startNewActivity
import com.demo.demofirebasechat.ui.login.LoginActivity
import com.demo.demofirebasechat.ui.register.RegisterActivity
import com.demo.demofirebasechat.ui.userList.adapter.UserAdapter
import com.google.firebase.firestore.FirebaseFirestore
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