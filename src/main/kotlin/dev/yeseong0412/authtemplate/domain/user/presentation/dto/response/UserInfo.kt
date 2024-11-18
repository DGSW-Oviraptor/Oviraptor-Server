package dev.yeseong0412.authtemplate.domain.user.presentation.dto.response

import dev.yeseong0412.authtemplate.domain.user.domain.enums.UserRoles

data class UserInfo(
    val email: String = "",
    val name: String = "",
    val role: UserRoles
)