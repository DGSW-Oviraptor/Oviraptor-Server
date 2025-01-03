package dev.yeseong0412.authtemplate.domain.user.presentation

import dev.yeseong0412.authtemplate.domain.user.presentation.dto.response.UserInfo
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.*
import dev.yeseong0412.authtemplate.domain.user.service.UserService
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.common.annotation.GetAuthenticatedId
import dev.yeseong0412.authtemplate.global.security.jwt.dto.JwtInfo
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {

    @Operation(summary = "내 정보")
    @GetMapping("/info")
    fun getUserInfo(@GetAuthenticatedId userId: Long): BaseResponse<UserInfo> {
        return userService.getUserInfo(userId)
    }

    @Operation(summary = "이름 수정")
    @PatchMapping("/info/name")
    fun changeUsername(@GetAuthenticatedId userId: Long, @RequestParam username: String): BaseResponse<Unit> {
        return userService.changeUsername(userId, username)
    }

    @Operation(summary = "이메일 수정")
    @PatchMapping("/info/email")
    fun changeEmail(@GetAuthenticatedId userId: Long, @RequestBody request: ChangeEmailRequest): BaseResponse<JwtInfo> {
        return userService.changeEmail(userId, request)
    }

    @Operation(summary = "비밀번호 수정")
    @PatchMapping("/info/password")
    fun changePassword(
        @GetAuthenticatedId userId: Long,
        @RequestBody changePasswordRequest: ChangePasswordRequest
    ): BaseResponse<Unit> {
        return userService.changePassword(userId, changePasswordRequest)
    }

    @Operation(summary = "친구추가")
    @PostMapping("/friends")
    fun addFriend(@GetAuthenticatedId userId: Long, @RequestParam email: String): BaseResponse<Unit> {
        return userService.addFriend(userId, email)
    }

    @Operation(summary = "친구목록")
    @GetMapping("/friends")
    fun getAllFriends(@GetAuthenticatedId userId: Long): BaseResponse<List<UserInfo>> {
        return userService.getAllFriends(userId)
    }

    @Operation(summary = "친구 삭제")
    @DeleteMapping("/friends")
    fun deleteFriend(@GetAuthenticatedId userId: Long, @RequestParam email: String): BaseResponse<Unit> {
        return userService.deleteFriend(userId, email)
    }

    @Operation(summary = "이름으로 검색")
    @GetMapping("/search")
    fun getAllFriends(@RequestParam username: String): BaseResponse<List<UserInfo>> {
        return userService.searchByUsername(username)
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping
    fun deleteUser(@GetAuthenticatedId userId: Long, @RequestBody request: DeleteUserRequest): BaseResponse<Unit> {
        return userService.deleteUser(userId, request)
    }
}