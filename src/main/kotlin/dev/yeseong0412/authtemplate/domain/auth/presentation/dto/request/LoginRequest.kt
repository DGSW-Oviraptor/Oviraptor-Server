package dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request

data class LoginRequest(
    val email: String = "",
    val password: String = ""
)