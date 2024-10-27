package dev.yeseong0412.authtemplate.domain.chat.presentation

import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatRoomEntity
import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoom
import dev.yeseong0412.authtemplate.domain.chat.domain.service.ChatRoomService
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.ChatMessage
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.ChatOnline
import dev.yeseong0412.authtemplate.global.auth.jwt.JwtUtils
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat")
class ChatRoomController(
    val chatRoomService: ChatRoomService,
    val jwtUtils: JwtUtils
) {

    @GetMapping("/rooms")
    fun getAllRooms(): MutableList<ChatRoomEntity> = chatRoomService.getAllRooms()

    @PostMapping("/rooms")
    fun createRoom(@RequestParam name: String): BaseResponse<ChatRoom> = chatRoomService.createRoom(name)

    @PostMapping("/rooms/{roomId}/invite")
    fun inviteToRoom(@PathVariable roomId: Long, @RequestParam participant: String): BaseResponse<ChatRoom> {
        return chatRoomService.inviteToRoom(roomId, participant)
    }

    @DeleteMapping("/rooms/{roomId}")
    fun deleteRoom(@PathVariable roomId: Long): BaseResponse<Unit> {
        return chatRoomService.deleteRoom(roomId)
    }

    // 채팅방 입장
    @MessageMapping("/enter/{roomId}")
    @SendTo("/topic/room/{roomId}")
    fun enterRoom(@PathVariable roomId: Long, username: String): String {
        return chatRoomService.enterRoom(roomId, username)
    }

    // 채팅방 퇴장
    @MessageMapping("/exit/{roomId}")
    @SendTo("/topic/room/{roomId}")
    fun exitRoom(@PathVariable roomId: Long, username: String): String {
        return chatRoomService.exitRoom(roomId, username)
    }

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/room/{roomId}")
    fun sendMessage(@PathVariable roomId: String, message: ChatMessage): ChatOnline {
        val toMessage = ChatOnline(writer = jwtUtils.getUsername(message.token), message = message.message)
        return toMessage
    }
}