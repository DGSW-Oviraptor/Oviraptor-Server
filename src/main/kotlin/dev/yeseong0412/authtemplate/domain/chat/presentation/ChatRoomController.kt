package dev.yeseong0412.authtemplate.domain.chat.presentation

import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoomIdInfo
import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoomInfo
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.ChatMessage
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.ChatOnline
import dev.yeseong0412.authtemplate.domain.chat.service.ChatRoomService
import dev.yeseong0412.authtemplate.global.auth.jwt.JwtUtils
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.common.annotation.GetAuthenticatedId
import io.swagger.v3.oas.annotations.Operation
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat")
class ChatRoomController(
    val chatRoomService: ChatRoomService,
    val jwtUtils: JwtUtils
) {

    @Operation(summary = "방 목록")
    @GetMapping("/rooms")
    fun getAllRooms(): List<ChatRoomIdInfo> = chatRoomService.getAllRooms()

    @Operation(summary = "방 생성")
    @PostMapping("/create")
    fun createRoom(@RequestParam name: String, @GetAuthenticatedId userId: Long): BaseResponse<ChatRoomInfo> {
        return chatRoomService.createRoom(name, userId)
    }

    @Operation(summary = "초대")
    @PostMapping("/rooms/{roomId}/invite")
    fun inviteToRoom(@PathVariable roomId: Long, @RequestParam participant: String): BaseResponse<ChatRoomInfo> {
        return chatRoomService.inviteToRoom(roomId, participant)
    }

    @Operation(summary = "방 삭제")
    @DeleteMapping("/rooms/{roomId}")
    fun deleteRoom(@PathVariable roomId: Long): BaseResponse<Unit> {
        return chatRoomService.deleteRoom(roomId)
    }

    // 채팅방 입장
    @MessageMapping("/enter/{roomId}")
    @SendTo("/topic/room/{roomId}")
    fun enterRoom(@PathVariable roomId: Long, @GetAuthenticatedId userId: Long): String {
        println(roomId)
        println("enter")
        return chatRoomService.enterRoom(roomId, userId)
    }

    // 채팅방 퇴장
    @MessageMapping("/exit/{roomId}")
    @SendTo("/topic/room/{roomId}")
    fun exitRoom(@PathVariable roomId: Long, @GetAuthenticatedId userId: Long): String {
        println(roomId)
        println("exit")
        return chatRoomService.exitRoom(roomId, userId)
    }

//    @MessageMapping("/chat/{roomId}")
//    @SendTo("/topic/room/{roomId}")
//    fun sendMessage(@PathVariable roomId: String, token : String, message : String): ChatOnline {
//        val toMessage = ChatOnline(writer = jwtUtils.getUsername(token), message = message)
//        return toMessage
//    }

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/room/{roomId}")
    fun sendMessage(@PathVariable roomId: String, message: ChatMessage): ChatOnline {
        // JWT 토큰을 추출하여 사용자 이름을 얻습니다.
        val username = jwtUtils.getUsername(message.token) // message.token에 공백이 포함되지 않도록 확인하세요
        val toMessage = ChatOnline(writer = username, message = message.message)
        println(roomId)
        println(message.message)
        println(message.token)
        return toMessage
    }
}