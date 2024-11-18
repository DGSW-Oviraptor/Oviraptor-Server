package dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response

data class ChatMessageInfo(
    val chatId: Long,
    val room: String = "",
    val writer: String = "",
    val content: String = "",
    val isMine: Boolean = false
)