package dev.yeseong0412.authtemplate.domain.chat.domain.model

data class ChatMessageInfo(
    val chatId: Long,
    val room: String = "",
    val writer: String = "",
    val content: String = "",
    val isMine: Boolean = false
)