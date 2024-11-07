package dev.yeseong0412.authtemplate.domain.chat.service

import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoomIdInfo
import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoomInfo
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.ChatMessage
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.ChatOnline
import dev.yeseong0412.authtemplate.global.common.BaseResponse

interface ChatRoomService {
    fun getAllRooms(): BaseResponse<List<ChatRoomIdInfo>>
    fun createRoom(name: String, userId: Long): BaseResponse<ChatRoomInfo>
    fun inviteToRoom(roomId: Long, userEmail: String): BaseResponse<ChatRoomInfo>
    fun deleteRoom(roomId: Long): BaseResponse<Unit>
    fun enterRoom(roomId: Long, userId: Long): ChatOnline
    fun exitRoom(roomId: Long, userId: Long): ChatOnline
    fun sendChat(roomId: Long, token : String, message: ChatMessage) : ChatOnline
//    fun getAllMessages(roomId: Long): BaseResponse<List<ChatMessageEntity>>
}