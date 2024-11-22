package dev.yeseong0412.authtemplate.domain.user.service

import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatRoomInfo
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.response.UserInfo
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.*
import dev.yeseong0412.authtemplate.global.auth.jwt.JwtInfo
import dev.yeseong0412.authtemplate.global.common.BaseResponse

interface UserService {
    fun registerUser(registerUserRequest: RegisterUserRequest): BaseResponse<Unit>
    fun loginUser(loginRequest: LoginRequest): BaseResponse<JwtInfo>
    fun refreshToken(refreshRequest: RefreshRequest): BaseResponse<String>
    fun getAllRooms(userId: Long): BaseResponse<List<ChatRoomInfo>>
    fun getUserInfo(userId: Long): BaseResponse<UserInfo>
    fun changeUserInfo(userId: Long, changeInfoRequest: ChangeInfoRequest): BaseResponse<UserInfo>
    fun changePassword(userId: Long, changePasswordRequest: ChangePasswordRequest): BaseResponse<Unit>
    fun addFriend(userId: Long, email: String): BaseResponse<UserInfo>
    fun getAllFriends(userId: Long): BaseResponse<List<UserInfo>>
    fun deleteFried(userId: Long, email: String): BaseResponse<Unit>
    fun searchByUsername(username: String) : BaseResponse<List<UserInfo>>
    fun sendMail(email: String): BaseResponse<Unit>
    fun deleteUser(userId: Long, deleteUserRequest: DeleteUserRequest): BaseResponse<Unit>
}