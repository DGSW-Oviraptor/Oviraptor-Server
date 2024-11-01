package dev.yeseong0412.authtemplate.domain.user.presentation

import dev.yeseong0412.authtemplate.domain.user.domain.entity.UserEntity
import dev.yeseong0412.authtemplate.domain.user.domain.model.UserInfo
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.ChangeInfoRequest
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.LoginRequest
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.RefreshRequest
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.RegisterUserRequest
import dev.yeseong0412.authtemplate.domain.user.service.UserService
import dev.yeseong0412.authtemplate.global.auth.jwt.JwtInfo
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.common.annotation.GetAuthenticatedId
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
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

    @Operation(summary = "내 정보")
    @GetMapping("/info")
    fun getUserInfo(@GetAuthenticatedId userId: Long): BaseResponse<UserInfo> {
        return userService.getUserInfo(userId)
    }

    @Operation(summary = "내 정보 수정")
    @PatchMapping("/info")
    fun changeUserInfo(@GetAuthenticatedId userId: Long, @RequestBody changeInfoRequest: ChangeInfoRequest): BaseResponse<UserEntity> {
        return userService.changeUserInfo(userId, changeInfoRequest)
    }

    @Operation(summary = "친구추가")
    @PostMapping("/friends/add")
    fun addFriend(@GetAuthenticatedId userId: Long, userEmail: String): BaseResponse<UserEntity> {
        return userService.addFriend(userId, userEmail)
    }

    @Operation(summary = "친구목록")
    @GetMapping("/friends")
    fun getAllFriends(@GetAuthenticatedId userId: Long): MutableList<UserEntity> {
        return userService.getAllFriends(userId)
    }
}