package dev.yeseong0412.authtemplate.domain.chat.domain.model

data class ChatRoomInfo(
    val name: String = "",
    val participants: List<String> = listOf()
)