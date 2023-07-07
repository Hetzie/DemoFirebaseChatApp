package com.demo.demofirebasechat.ui.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.demofirebasechat.base.BaseViewModel
import com.demo.demofirebasechat.datastore.MyDataStore
import com.demo.demofirebasechat.extensions.Extensions
import com.demo.demofirebasechat.extensions.showToast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    var context: Context,
    var myDataStore: MyDataStore,
) : BaseViewModel<Any>() {
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val db = FirebaseFirestore.getInstance()

    fun loginUser() {
        val dbData = db.collection("Users").document(username.value!!)
        dbData.get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document.exists()) {

                        if (document["password"] == password.value) {
//                            dbData.update("deviceToken", Extensions.FCM_TOKEN)
                            viewModelScope.launch {
                                withContext(Dispatchers.IO) {
                                    myDataStore.setUserNameData(username.value!!)
                                    myDataStore.setDisplayNameData(
                                        document.get("displayName").toString()
                                    )
                                    myDataStore.setIsLogin(true)
                                }
                            }
                        } else {
                            context.showToast("Incorrect username or password.")
                        }

                    } else {
                        context.showToast("User do not exist.")
                    }
                }
            }
    }
}