package com.demo.demofirebasechat.ui.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.demofirebasechat.base.BaseViewModel
import com.demo.demofirebasechat.data.dummy.UserProfile
import com.demo.demofirebasechat.datastore.MyDataStore
import com.demo.demofirebasechat.extentions.showToast
import com.demo.demofirebasechat.ui.userList.UserListActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    var context: Context,
    var myDataStore: MyDataStore
    ):BaseViewModel<Any>() {
    val username = MutableLiveData<String>()

    val db = FirebaseFirestore.getInstance()

    fun loginUser() {
        val dbData = db.collection("Users").document(username.value!!)
        dbData.get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document.exists()) {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {

                                val i = Intent(context, UserListActivity::class.java)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(i)
                            }

                            withContext(Dispatchers.IO){
                                myDataStore.setUserNameData(username.value!!)
                                myDataStore.setDisplayNameData(document.get("displayName").toString())
                                myDataStore.setIsLogin(true)
                            }
                        }
                    } else {
                        context.showToast("User do not exist.")
                    }
                }
            }
    }
}