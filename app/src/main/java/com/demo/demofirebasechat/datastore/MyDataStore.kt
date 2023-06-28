package com.demo.demofirebasechat.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class MyDataStore(var context: Context) {

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        var USERNAME = stringPreferencesKey("USER__NAME")
        var DISPLAYNAME = stringPreferencesKey("DISPLAY_NAME")
        var ISLOGIN = booleanPreferencesKey("IS_LOGIN")

    }

    suspend fun setUserNameData(username: String) {
        context.dataStore.edit {
            it[USERNAME] = username
            Log.e("setUserNameData", username)
        }
    }

    suspend fun setDisplayNameData(displayName: String) {
        context.dataStore.edit {
            it[DISPLAYNAME] = displayName
            Log.e("setDisplayNameData", displayName)

        }
    }
    suspend fun setIsLogin(isLogin: Boolean) {
        context.dataStore.edit {
            it[ISLOGIN] = isLogin

        }
    }


    val getUserName: Flow<String> = context.dataStore.data.map {
        it[USERNAME] ?: ""

    }
    val getDisplayName: Flow<String> = context.dataStore.data.map {
        it[DISPLAYNAME] ?: ""

    }
    val getIsLogin: Flow<Boolean> = context.dataStore.data.map {
        it[ISLOGIN] ?: false

    }

    fun usernameValue(lifecycleOwner: LifecycleOwner): String {
        var result = ""
        getUserName.asLiveData().observe(lifecycleOwner) {
             result = it
        }
        return result
    }
    fun displayNameValue(lifecycleOwner: LifecycleOwner): String {
        var result = ""
        getDisplayName.asLiveData().observe(lifecycleOwner) {
             result = it
        }
        return result
    }

    suspend fun clearSession(){
        setUserNameData("")
        setDisplayNameData("")
        setIsLogin(false)
    }
}