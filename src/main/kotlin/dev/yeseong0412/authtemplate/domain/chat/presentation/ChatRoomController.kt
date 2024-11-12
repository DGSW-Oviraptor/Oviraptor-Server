package dev.yeseong0412.authtemplate.domain.chat.presentation

import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatMessageInfo
import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoomIdInfo
import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoomInfo
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.ChatMessage
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.ChatOnline
import dev.yeseong0412.authtemplate.domain.chat.service.ChatRoomService
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.common.annotation.GetAuthenticatedId
import io.swagger.v3.oas.annotations.Operation
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat")
class ChatRoomController(
    val chatRoomService: ChatRoomService
) {

    @Operation(summary = "방 목록")
    @GetMapping("/rooms")
    fun getAllRooms(): BaseResponse<List<ChatRoomIdInfo>> = chatRoomService.getAllRooms()

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

    @MessageMapping("/enter/{roomId}")
    @SendTo("/topic/room/{roomId}")
    fun enterRoom(@DestinationVariable roomId: Long, @GetAuthenticatedId userId: Long): ChatOnline {
        println(roomId)
        println("enter")
        return chatRoomService.enterRoom(roomId = roomId, userId = userId)
    }

    @Operation(summary = "채팅 불러오기")
    @GetMapping("/{roomId}")
    fun getAllMessages(@PathVariable roomId: Long, @GetAuthenticatedId userId: Long): BaseResponse<List<ChatMessageInfo>> {
        return chatRoomService.getAllMessages(roomId, userId)
    }

    @MessageMapping("/exit/{roomId}")
    @SendTo("/topic/room/{roomId}")
    fun exitRoom(@DestinationVariable roomId: Long, @GetAuthenticatedId userId: Long): ChatOnline {
        println(roomId)
        println("exit")
        return chatRoomService.exitRoom(roomId, userId)
    }

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/room/{roomId}")
    fun sendMessage(
        @DestinationVariable roomId: Long,
        @Header("Authorization") token: String,
        message: ChatMessage
    ): ChatOnline {
        return chatRoomService.sendChat(roomId = roomId, token = token, message = message)
    }
}