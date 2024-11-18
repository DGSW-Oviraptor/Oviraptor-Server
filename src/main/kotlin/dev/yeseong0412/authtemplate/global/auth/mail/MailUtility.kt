package dev.yeseong0412.authtemplate.global.auth.mail

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.util.*

@Component
class MailUtility(
    val javaMailSender: JavaMailSender
) {

    fun getRandomString(length: Int): String {
        val randomUUID = UUID.randomUUID().toString()
        return randomUUID.substring(0, length)
    }

    fun sendMail(email: String): String {
        //인증 번호 만들기
        val randomString = getRandomString(6)

        //이메일 발송하기
        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message)
        helper.setTo(email)
        helper.setSubject("나르샤 채팅 이메일 인증")
        helper.setText("인증 코드 : $randomString")
        helper.setFrom("{email}")
        javaMailSender.send(message)

        return randomString
    }
}