package dev.yeseong0412.authtemplate.domain.mail.service

import dev.yeseong0412.authtemplate.global.common.BaseResponse

interface MailService {
    fun getRandomString(length: Int): String
    fun sendMail(email: String): BaseResponse<Unit>
    fun isValidEmail(email: String): Boolean
}