package com.demo.demofirebasechat.data.dummy

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserProfile(
    @set:PropertyName("user_name")
    var userName: String = "",
    var displayName: String = "",
    var password: String = ""
//    var image: String = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSyHbN-vM0IgUbHcT0CuE64gjgueTAgFyr_3ElBNpIGpmi4Xvfg"
)

@Entity
data class ChatModel(
    var senderUsername: String = "",
    var receiverUserName: String = "",
    var createdAt: Date?=Date(),
    @PrimaryKey
    var chatId: String = "Chat:$senderUsername,$receiverUserName",
    var textMessage: String="",
    var status: Int=0,//0=sending,1=sent,2=delivered,3=seen,4=failed
)

data class Message(
    @ServerTimestamp
    var textMessage: String = ""
)


