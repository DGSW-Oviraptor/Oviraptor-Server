package dev.yeseong0412.authtemplate.domain.user.domain.repository

import dev.yeseong0412.authtemplate.domain.user.domain.model.User
import dev.yeseong0412.authtemplate.global.auth.jwt.JwtUserDetails
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserAuthHolder {
    fun current(): User {
        return (SecurityContextHolder.getContext().authentication.principal as JwtUserDetails).user
    }
}