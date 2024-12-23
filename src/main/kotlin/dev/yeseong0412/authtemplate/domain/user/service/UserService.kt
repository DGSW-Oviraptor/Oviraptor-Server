package dev.yeseong0412.authtemplate.domain.user.service

import dev.yeseong0412.authtemplate.domain.user.presentation.dto.response.UserInfo
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.*
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.security.jwt.dto.JwtInfo

interface UserService {
    fun getUserInfo(userId: Long): BaseResponse<UserInfo>
    fun changeUsername(userId: Long, username: String): BaseResponse<Unit>
    fun changeEmail(userId: Long, request: ChangeEmailRequest): BaseResponse<JwtInfo>
    fun changePassword(userId: Long, changePasswordRequest: ChangePasswordRequest): BaseResponse<Unit>
    fun addFriend(userId: Long, email: String): BaseResponse<Unit>
    fun getAllFriends(userId: Long): BaseResponse<List<UserInfo>>
    fun deleteFriend(userId: Long, email: String): BaseResponse<Unit>
    fun searchByUsername(username: String): BaseResponse<List<UserInfo>>
    fun deleteUser(userId: Long, deleteUserRequest: DeleteUserRequest): BaseResponse<Unit>
}