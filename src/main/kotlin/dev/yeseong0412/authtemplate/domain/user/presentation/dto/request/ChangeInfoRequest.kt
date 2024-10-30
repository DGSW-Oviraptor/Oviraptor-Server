package dev.yeseong0412.authtemplate.domain.user.presentation.dto.request

data class ChangeInfoRequest(
    val email: String = "",
    val name: String = "",
    val password: String = ""
)