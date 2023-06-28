package com.demo.demofirebasechat.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.demo.demofirebasechat.BR
import com.demo.demofirebasechat.R
import com.demo.demofirebasechat.base.BaseActivity
import com.demo.demofirebasechat.databinding.ActivityRegisterBinding
import com.demo.demofirebasechat.extentions.startNewActivity
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
        }
        return userNameError && displayNameError
    }
}