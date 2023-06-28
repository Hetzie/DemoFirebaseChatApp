package com.demo.demofirebasechat.ui.userList

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.demo.demofirebasechat.base.BaseViewModel
import com.demo.demofirebasechat.data.dummy.UserProfile
import com.demo.demofirebasechat.datastore.MyDataStore
import com.demo.demofirebasechat.ui.login.LoginActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserListViewModel@Inject constructor(
    var context: Context):BaseViewModel<Any>() {
    val db = FirebaseFirestore.getInstance()
    val myDataStore = MyDataStore(context)

    fun insertUser(userName: String, displayName: String){

        val profile = UserProfile(userName, displayName)


        db.collection("Users").document(userName)
            .set(profile, SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun logOut(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                myDataStore.clearSession()
            }
            withContext(Dispatchers.Main){
                val i = Intent(context, LoginActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
            }
        }
    }
}