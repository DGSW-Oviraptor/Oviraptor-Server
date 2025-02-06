package dev.yeseong0412.authtemplate.domain.auth.presentation.dto.response

data class LoginResponse(
    val accessToken: String = "",
    val refreshToken: String = "",
    val username: String = ""
)
