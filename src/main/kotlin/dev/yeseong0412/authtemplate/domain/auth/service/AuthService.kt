package dev.yeseong0412.authtemplate.domain.auth.service

import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request.LoginRequest
import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request.RefreshRequest
import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request.RegisterUserRequest
import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.response.LoginResponse
import dev.yeseong0412.authtemplate.global.common.BaseResponse

interface AuthService {
    fun registerUser(registerUserRequest: RegisterUserRequest): BaseResponse<Unit>
    fun loginUser(loginRequest: LoginRequest): BaseResponse<LoginResponse>
    fun refreshToken(refreshRequest: RefreshRequest): BaseResponse<String>
}