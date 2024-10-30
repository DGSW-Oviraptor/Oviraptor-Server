package dev.yeseong0412.authtemplate.domain.user.domain.model

import dev.yeseong0412.authtemplate.domain.user.domain.entity.UserEntity

data class UserInfo(
    val id: Long? = null,
    val email: String = "",
    val username: String = "",
    val friends: MutableSet<UserEntity> = mutableSetOf()
)