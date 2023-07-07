package com.demo.demofirebasechat.ui.register

import android.os.Bundle
import androidx.lifecycle.asLiveData
import com.demo.demofirebasechat.BR
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.base.BaseActivity
import com.demo.demofirebasechat.databinding.ActivityRegisterBinding
import com.demo.demofirebasechat.extensions.startNewActivity
import com.demo.demofirebasechat.ui.login.LoginActivity
import com.demo.demofirebasechat.ui.userList.UserListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>() {

    override val layoutId: Int
        get() = R.layout.activity_register
    override val bindingVariable: Int
        get() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        onClicks()
        myDataStore.getIsLogin.asLiveData().observe(this) {
            if (it) {
                startNewActivity(UserListActivity::class.java, clearTask = true)
            }
        }
    }

    private fun onClicks() {
        binding.apply {
            btnRegister.setOnClickListener {
                if (validate()) {
                    mViewModel.registerUser()
                }
            }

            tvLogin.setOnClickListener {
                startNewActivity(LoginActivity::class.java, finish = true)
            }
        }
    }

    override fun setupObservable() {}

    private fun validate(): Boolean {
        var userNameError = true
        var displayNameError = true
        var passwordError = true
        binding.apply {
            tilUsername.helperText = if (etUsername.text.toString().trim().isEmpty()) {
                userNameError = false
                "Please enter username"
            } else {
                null
            }

            tilDisplayName.helperText = if (etDisplayName.text.toString().trim().isEmpty()) {
                displayNameError = false
                "Please enter display name"
            } else {
                null
            }

            tilPassword.helperText = if (etPassword.text.toString().trim().isEmpty()) {
                passwordError = false
                "Please enter password"
            }else if (etPassword.text.toString().length<8) {
                passwordError = false
                "Password must contain minimum 8 characters"
            } else {
                null
            }
        }
        return userNameError && displayNameError && passwordError
    }
}