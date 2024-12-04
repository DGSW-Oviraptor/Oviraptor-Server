package dev.yeseong0412.authtemplate.domain.chat.presentation

import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatMessageInfo
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatRoomInfo
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.request.ChatMessage
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatOnline
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatRoom
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
    fun getAllRooms(): BaseResponse<List<ChatRoom>> = chatRoomService.getAllRooms()

    @Operation(summary = "방 생성")
    @PostMapping
    fun createRoom(@RequestParam name: String, @GetAuthenticatedId userId: Long): BaseResponse<Unit> {
        return chatRoomService.createRoom(name, userId)
    }

    @Operation(summary = "초대")
    @PostMapping("/rooms/{roomId}/invite")
    fun inviteToRoom(@PathVariable roomId: Long, @RequestParam participant: String): BaseResponse<Unit> {
        return chatRoomService.inviteToRoom(roomId, participant)
    }

    @Operation(summary = "방 나가기")
    @DeleteMapping("/rooms/{roomId}/leave")
    fun leaveRoom(@PathVariable roomId: Long, @GetAuthenticatedId userId: Long): BaseResponse<Unit> {
        return chatRoomService.leaveRoom(roomId, userId)
    }

    @Operation(summary = "방 삭제")
    @DeleteMapping("/rooms/{roomId}")
    fun deleteRoom(@PathVariable roomId: Long, @GetAuthenticatedId userId: Long): BaseResponse<Unit> {
        return chatRoomService.deleteRoom(roomId = roomId, userId = userId)
    }

    @Operation(summary = "방 정보")
    @GetMapping("/info/{roomId}")
    fun getRoomInfo(@PathVariable roomId: Long): BaseResponse<ChatRoomInfo> {
        return chatRoomService.getRoomInfo(roomId)
    }

    @MessageMapping("/enter/{roomId}")
    @SendTo("/topic/room/{roomId}")
    fun enterRoom(@DestinationVariable roomId: Long, @GetAuthenticatedId userId: Long): ChatOnline {
        return chatRoomService.enterRoom(roomId = roomId, userId = userId)
    }

    @Operation(summary = "채팅 불러오기", description = "처음 실행할때는 objectId 필요 없음")
    @GetMapping("/{roomId}")
    fun getAllMessages(
        @PathVariable roomId: Long,
        @RequestParam(required = false) objectId: String?,
        @GetAuthenticatedId userId: Long
    ): BaseResponse<List<ChatMessageInfo>> {
        return chatRoomService.getAllMessages(roomId, objectId, userId)
    }

    @MessageMapping("/exit/{roomId}")
    @SendTo("/topic/room/{roomId}")
    fun exitRoom(@DestinationVariable roomId: Long, @GetAuthenticatedId userId: Long): ChatOnline {
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