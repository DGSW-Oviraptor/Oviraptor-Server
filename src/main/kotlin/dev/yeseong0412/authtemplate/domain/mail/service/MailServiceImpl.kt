package dev.yeseong0412.authtemplate.domain.mail.service

import dev.yeseong0412.authtemplate.domain.auth.exception.EmailErrorCode
import dev.yeseong0412.authtemplate.domain.mail.domain.repository.MailRepository
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.exception.CustomException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.util.*

@Service
class MailServiceImpl(
    private val mailRepository: MailRepository,
    private val javaMailSender: JavaMailSender
) : MailService {

    override fun getRandomString(length: Int): String {
        val randomUUID = UUID.randomUUID().toString()
        return randomUUID.substring(0, length)
    }

    override fun sendMail(email: String): BaseResponse<Unit> {
        if (!isValidEmail(email)) {
            throw CustomException(EmailErrorCode.EMAIL_INVALID)
        }

        val randomString = getRandomString(6)
        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message)

        helper.setTo(email)
        helper.setSubject("나르샤 채팅 이메일 인증")
        helper.setText("인증 코드 : $randomString")
        helper.setFrom("{email}")

        javaMailSender.send(message)
        mailRepository.save(email, randomString)

        return BaseResponse(
            message = "success"
        )
    }

    override fun isValidEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex() // 이메일 형식 검증
        return email.matches(regex)
    }
}