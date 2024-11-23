package dev.yeseong0412.authtemplate.domain.user.presentation.dto.request

data class ChangeEmailRequest(
    val email: String,
    val authCode: String
)
