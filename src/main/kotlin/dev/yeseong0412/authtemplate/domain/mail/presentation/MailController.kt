package dev.yeseong0412.authtemplate.domain.mail.presentation

import dev.yeseong0412.authtemplate.domain.mail.service.MailService
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mail")
class MailController(
    private val mailService: MailService
) {

    @Operation(summary = "인증코드 받기")
    @GetMapping("/code")
    fun sendMail(
        @RequestParam email: String
    ): BaseResponse<Unit> {
        return mailService.sendMail(email)
    }
}