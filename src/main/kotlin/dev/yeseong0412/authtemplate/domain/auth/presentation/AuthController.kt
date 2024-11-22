package dev.yeseong0412.authtemplate.domain.auth.presentation

import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request.LoginRequest
import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request.RefreshRequest
import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request.RegisterUserRequest
import dev.yeseong0412.authtemplate.domain.auth.service.AuthService
import dev.yeseong0412.authtemplate.global.security.jwt.dto.JwtInfo
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@Tag(name = "auth", description = "유저 인증/인가")
class AuthController(
    private val authService: AuthService
) {
    @Operation(summary = "회원가입")
    @PostMapping("/register")
    fun registerUser(
        @RequestBody registerUserRequest: RegisterUserRequest
    ): BaseResponse<Unit> {
        return authService.registerUser(registerUserRequest)
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequest: LoginRequest): BaseResponse<JwtInfo> {
        return authService.loginUser(loginRequest)
    }

    @Operation(summary = "토큰 리프레시")
    @PostMapping("/refresh")
    fun refreshUser(@RequestBody refreshRequest: RefreshRequest): BaseResponse<String> {
        return authService.refreshToken(refreshRequest)
    }

    @Operation(summary = "인증코드 받기")
    @GetMapping("/email")
    fun sendMail(
        @RequestParam email: String
    ): BaseResponse<Unit> {
        return authService.sendMail(email)
    }
}