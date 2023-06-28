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
//    var image: String = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSyHbN-vM0IgUbHcT0CuE64gjgueTAgFyr_3ElBNpIGpmi4Xvfg"
)

@Entity
data class ChatModel(
    var senderUsername: String = "",
    var receiverUserName: String = "",
    @PrimaryKey
    var ChatId: String = "Chat:$senderUsername,$receiverUserName",
    var messageList: MutableList<Message> = mutableListOf(),
    var messageListSize: Int = messageList.size
)

data class Message(
    @ServerTimestamp
    var createdAt: Date?=Date(),
    var textMessage: String = ""
)


