package com.demo.demofirebasechat

import com.demo.demofirebasechat.data.dummy.PushNotification
import com.demo.demofirebasechat.extensions.Extensions
import com.demo.demofirebasechat.extensions.Extensions.Companion.CONTENT_TYPE
import com.demo.demofirebasechat.extensions.Extensions.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

class RetrofitInstance {

    companion object{
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Extensions.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationApi::class.java)
        }
    }
}

interface NotificationApi {

    @Headers("Authorization: key=$SERVER_KEY","Content-type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}