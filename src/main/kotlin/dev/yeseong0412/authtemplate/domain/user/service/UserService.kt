package dev.yeseong0412.authtemplate.domain.user.service

import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoomInfo
import dev.yeseong0412.authtemplate.domain.user.domain.model.UserInfo
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.*
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.response.SendMailResponse
import dev.yeseong0412.authtemplate.global.auth.jwt.JwtInfo
import dev.yeseong0412.authtemplate.global.common.BaseResponse

interface UserService {
    fun registerUser(registerUserRequest: RegisterUserRequest): BaseResponse<Unit>
    fun loginUser(loginRequest: LoginRequest): BaseResponse<JwtInfo>
    fun refreshToken(refreshRequest: RefreshRequest): BaseResponse<String>
    fun getAllRooms(userId: Long): BaseResponse<List<ChatRoomInfo>>
    fun getUserInfo(userId: Long): BaseResponse<UserInfo>
    fun changeUserInfo(userId: Long, changeInfoRequest: ChangeInfoRequest): BaseResponse<UserInfo>
    fun addFriend(userId: Long, friendRequest: FriendRequest): BaseResponse<UserInfo>
    fun getAllFriends(userId: Long): BaseResponse<List<UserInfo>>
    fun searchByUsername(username: String) : BaseResponse<List<UserInfo>>
    fun searchByUserName(userName: String) : BaseResponse<List<UserInfo>>
    fun sendMail(email:String): SendMailResponse
}