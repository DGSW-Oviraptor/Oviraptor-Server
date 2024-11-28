package dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response

import dev.yeseong0412.authtemplate.domain.user.presentation.dto.response.ParticipantsInfo

data class ChatRoomInfo(
    val id: Long? = null,
    val name: String = "",
    val participants: List<ParticipantsInfo> = listOf()
)