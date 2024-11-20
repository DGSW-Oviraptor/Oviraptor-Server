package dev.yeseong0412.authtemplate.domain.user.presentation

import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatRoomInfo
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.response.UserInfo
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.*
import dev.yeseong0412.authtemplate.domain.user.service.UserService
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.common.annotation.GetAuthenticatedId
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {

    @Operation(summary = "내 방")
    @GetMapping("/rooms")
    fun getAllRooms(@GetAuthenticatedId userId: Long): BaseResponse<List<ChatRoomInfo>> {
        return userService.getAllRooms(userId)
    }

    @Operation(summary = "내 정보")
    @GetMapping("/info")
    fun getUserInfo(@GetAuthenticatedId userId: Long): BaseResponse<UserInfo> {
        return userService.getUserInfo(userId)
    }

    @Operation(summary = "내 정보 수정")
    @PatchMapping("/info")
    fun changeUserInfo(@GetAuthenticatedId userId: Long, @RequestBody changeInfoRequest: ChangeInfoRequest): BaseResponse<UserInfo> {
        return userService.changeUserInfo(userId, changeInfoRequest)
    }

    @Operation(summary = "비밀번호 수정")
    @PatchMapping("/info/password")
    fun changePassword(@GetAuthenticatedId userId: Long, @RequestBody changePasswordRequest: ChangePasswordRequest): BaseResponse<Unit> {
        return userService.changePassword(userId, changePasswordRequest)
    }

    @Operation(summary = "친구추가")
    @PostMapping("/friends/add")
    fun addFriend(@GetAuthenticatedId userId: Long, @RequestParam email: String): BaseResponse<UserInfo> {
        return userService.addFriend(userId, email)
    }

    @Operation(summary = "친구목록")
    @GetMapping("/friends")
    fun getAllFriends(@GetAuthenticatedId userId: Long): BaseResponse<List<UserInfo>> {
        return userService.getAllFriends(userId)
    }

    @Operation(summary = "이름으로 검색")
    @GetMapping("/search")
    fun getAllFriends(@RequestParam username : String): BaseResponse<List<UserInfo>> {
        return userService.searchByUsername(username)
    }
}