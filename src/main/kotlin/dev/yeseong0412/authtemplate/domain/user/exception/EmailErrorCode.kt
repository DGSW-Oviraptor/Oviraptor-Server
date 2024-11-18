package dev.yeseong0412.authtemplate.domain.user.exception

import dev.yeseong0412.authtemplate.global.exception.CustomErrorCode
import org.springframework.http.HttpStatus

enum class EmailErrorCode(
    override val status: HttpStatus,
    override val state: String,
    override val message: String
) : CustomErrorCode {
    EMAIL_INVALID(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "이메일이 올바르지 않습니다."),
    AUTHENTICODE_INVALID(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "인증코드가 올바르지 않습니다.")
}