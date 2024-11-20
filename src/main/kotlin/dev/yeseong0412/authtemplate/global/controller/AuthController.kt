package dev.yeseong0412.authtemplate.global.controller

import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.LoginRequest
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.RefreshRequest
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.RegisterUserRequest
import dev.yeseong0412.authtemplate.domain.user.service.UserService
import dev.yeseong0412.authtemplate.global.auth.jwt.JwtInfo
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@Tag(name = "auth", description = "유저 인증/인가")
class AuthController(
    private val userService: UserService
) {
    @Operation(summary = "회원가입")
    @PostMapping("/register")
    fun registerUser(
        @RequestBody registerUserRequest: RegisterUserRequest): BaseResponse<Unit> {
        return userService.registerUser(registerUserRequest)
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequest: LoginRequest): BaseResponse<JwtInfo> {
        return userService.loginUser(loginRequest)
    }

    @Operation(summary = "토큰 리프레시")
    @PostMapping("/refresh")
    fun refreshUser(@RequestBody refreshRequest: RefreshRequest): BaseResponse<String> {
        return userService.refreshToken(refreshRequest)
    }

    @Operation(summary = "인증코드 받기")
    @PostMapping("/email")
    fun sendMail(
        @RequestParam email : String
    ): BaseResponse<Unit> {
        return userService.sendMail(email)
    }
}