package dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request

data class RegisterUserRequest(
    val email: String = "",
    val name: String = "",
    val password: String = "",
    val authCode: String = ""
)
