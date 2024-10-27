package dev.yeseong0412.authtemplate.domain.chat.domain.model

data class ChatRoom(
    val id: Long? = null,
    val name: String = "",
    val participants: MutableList<String> = mutableListOf()
)