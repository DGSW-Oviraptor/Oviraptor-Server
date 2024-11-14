package dev.yeseong0412.authtemplate.domain.user.presentation.dto.request

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)