package com.demo.demofirebasechat.ui.login

import android.os.Bundle
import androidx.lifecycle.asLiveData
import com.demo.demofirebasechat.BR
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.base.BaseActivity
import com.demo.demofirebasechat.databinding.ActivityLoginBinding
import com.demo.demofirebasechat.extentions.startNewActivity
import com.demo.demofirebasechat.ui.register.RegisterActivity
import com.demo.demofirebasechat.ui.userList.UserListActivity
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
                startNewActivity(UserListActivity::class.java, clearTask = true)
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
        var passwordError = true
        binding.apply {
            tilUsername.helperText = if (etUsername.text.toString().trim().isEmpty()) {
                userNameError = false
                "Please enter username"
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
        return userNameError && passwordError
    }

}