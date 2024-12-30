package dev.yeseong0412.authtemplate.domain.chat.service

import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatMessageInfo
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatRoomInfo
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.request.ChatMessage
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatOnline
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatRoom
import dev.yeseong0412.authtemplate.global.common.BaseResponse

interface ChatRoomService {
    fun getAllRooms(userId: Long): BaseResponse<List<ChatRoom>>
    fun createRoom(name: String, userId: Long): BaseResponse<Unit>
    fun inviteToRoom(userId: Long, roomId: Long, userEmail: String): BaseResponse<Unit>
    fun leaveRoom(roomId: Long, userId: Long): BaseResponse<Unit>
    fun deleteRoom(roomId: Long, userId: Long): BaseResponse<Unit>
    fun getRoomInfo(roomId: Long): BaseResponse<ChatRoomInfo>
    fun enterRoom(roomId: Long, userId: Long): ChatOnline
    fun exitRoom(roomId: Long, userId: Long): ChatOnline
    fun sendChat(roomId: Long, token: String, message: ChatMessage): ChatOnline
    fun getAllMessages(roomId: Long, objectId: String?, userId: Long): BaseResponse<List<ChatMessageInfo>>
}