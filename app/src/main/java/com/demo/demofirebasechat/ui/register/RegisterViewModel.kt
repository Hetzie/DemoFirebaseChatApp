package com.demo.demofirebasechat.ui.register

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.demofirebasechat.base.BaseViewModel
import com.demo.demofirebasechat.data.dummy.UserProfile
import com.demo.demofirebasechat.datastore.MyDataStore
import com.demo.demofirebasechat.extensions.Extensions
import com.demo.demofirebasechat.extensions.showToast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    var context: Context,
    var myDataStore: MyDataStore,
) : BaseViewModel<Any>() {
    val username = MutableLiveData<String>()
    val displayName = MutableLiveData<String>()
    val password = MutableLiveData<String>()


    val db = FirebaseFirestore.getInstance()

    fun registerUser() {
        val profile = UserProfile(username.value!!, displayName.value!!, password.value!!)

        val dbData = db.collection("Users").document(username.value!!)
        dbData.get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document.exists()) {
                        context.showToast("Record already exists.")
                    } else {
                        dbData.set(profile)
                            .addOnSuccessListener {
//                                dbData.update("deviceToken", Extensions.FCM_TOKEN.value)
                                viewModelScope.launch {
                                    withContext(Dispatchers.IO) {
                                        myDataStore.setUserNameData(profile.userName)
                                        myDataStore.setDisplayNameData(profile.displayName)
                                        myDataStore.setIsLogin(true)
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                context.showToast(e.message.toString())
                            }
                    }
                }
            }
    }
}