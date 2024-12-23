package dev.yeseong0412.authtemplate.domain.user.presentation.dto.request

import com.fasterxml.jackson.annotation.JsonCreator

data class ChangePasswordRequest @JsonCreator constructor(
    val oldPassword: String,
    val newPassword: String
)