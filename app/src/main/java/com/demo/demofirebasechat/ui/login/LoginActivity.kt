package com.demo.demofirebasechat.ui.login

import android.os.Bundle
import androidx.lifecycle.asLiveData
import com.demo.demofirebasechat.BR
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.base.BaseActivity
import com.demo.demofirebasechat.databinding.ActivityLoginBinding
import com.demo.demofirebasechat.datastore.MyDataStore
import com.demo.demofirebasechat.extentions.startNewActivity
import com.demo.demofirebasechat.ui.register.RegisterActivity
import com.demo.demofirebasechat.ui.userList.UserListActivity
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {
    override val layoutId: Int
        get() = R.layout.activity_login
    override val bindingVariable: Int
        get() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        myDataStore.getIsLogin.asLiveData().observe(this) {
            if (it) {
                startNewActivity(UserListActivity::class.java, finish = true)
            }
        }
        onClicks()

    }

    private fun onClicks() {
        binding.apply {
            btnLogin.setOnClickListener {
                if (validate()) {
                    mViewModel.loginUser()
                }
            }

            tvRegister.setOnClickListener {
                startNewActivity(RegisterActivity::class.java, finish = true)
            }
        }
    }

    override fun setupObservable() {

    }

    private fun validate(): Boolean {
        var userNameError = true
        binding.apply {
            tilUsername.helperText = if (etUsername.text.toString().trim().isEmpty()) {
                userNameError = false
                "Please enter username"
            } else {
                null

            }
        }
        return userNameError
    }

}