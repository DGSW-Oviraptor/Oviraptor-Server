package dev.yeseong0412.authtemplate.domain.user.presentation.dto.request

import com.fasterxml.jackson.annotation.JsonCreator

data class FriendRequest @JsonCreator constructor(
    val email: String
)