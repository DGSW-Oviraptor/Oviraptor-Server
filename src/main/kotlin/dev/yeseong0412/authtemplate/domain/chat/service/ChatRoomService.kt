package dev.yeseong0412.authtemplate.domain.chat.service

import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatMessageInfo
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatRoomInfo
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.request.ChatMessage
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatOnline
import dev.yeseong0412.authtemplate.global.common.BaseResponse

interface ChatRoomService {
    fun getAllRooms(): BaseResponse<List<ChatRoomInfo>>
    fun createRoom(name: String, userId: Long): BaseResponse<ChatRoomInfo>
    fun inviteToRoom(roomId: Long, userEmail: String): BaseResponse<ChatRoomInfo>
    fun deleteRoom(roomId: Long): BaseResponse<Unit>
    fun getRoomInfo(roomId: Long): BaseResponse<ChatRoomInfo>
    fun enterRoom(roomId: Long, userId: Long): ChatOnline
    fun exitRoom(roomId: Long, userId: Long): ChatOnline
    fun sendChat(roomId: Long, token : String, message: ChatMessage) : ChatOnline
    fun getAllMessages(roomId: Long, lastId: String?, userId: Long): BaseResponse<List<ChatMessageInfo>>
}