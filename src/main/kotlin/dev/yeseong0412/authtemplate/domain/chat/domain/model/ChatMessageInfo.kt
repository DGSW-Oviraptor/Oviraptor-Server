package dev.yeseong0412.authtemplate.domain.chat.domain.model

import org.bson.types.ObjectId

data class ChatMessageInfo(
    val id: ObjectId? = null,
    val room: String = "",
    val writer: String = "",
    val content: String = "",
    val isMine: Boolean = false
)