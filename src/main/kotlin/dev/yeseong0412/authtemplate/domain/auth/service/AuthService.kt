package dev.yeseong0412.authtemplate.domain.auth.service

import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request.LoginRequest
import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request.RefreshRequest
import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request.RegisterUserRequest
import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.response.LoginResponse
import dev.yeseong0412.authtemplate.global.common.BaseResponse

interface AuthService {
    fun registerUser(request: RegisterUserRequest): BaseResponse<Unit>
    fun loginUser(request: LoginRequest): BaseResponse<LoginResponse>
    fun refreshToken(request: RefreshRequest): BaseResponse<String>
}