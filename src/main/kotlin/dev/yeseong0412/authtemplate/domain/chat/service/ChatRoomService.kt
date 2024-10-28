package dev.yeseong0412.authtemplate.domain.chat.service

import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatRoomEntity
import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoom
import dev.yeseong0412.authtemplate.global.common.BaseResponse

interface ChatRoomService {
    fun getAllRooms(): MutableList<ChatRoomEntity>
    fun createRoom(name: String): BaseResponse<ChatRoom>
    fun inviteToRoom(roomId: Long, userEmail: String): BaseResponse<ChatRoom>
    fun deleteRoom(roomId: Long): BaseResponse<Unit>
    fun enterRoom(roomId: Long, username: String): String
    fun exitRoom(roomId: Long, username: String): String
}