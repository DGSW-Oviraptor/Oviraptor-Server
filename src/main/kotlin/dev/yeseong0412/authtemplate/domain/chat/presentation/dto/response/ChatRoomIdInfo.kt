package dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response

data class ChatRoomIdInfo(
    val id: Long? = null,
    val name: String = "",
    val participants: List<String> = listOf()
)