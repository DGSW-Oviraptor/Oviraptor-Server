package dev.yeseong0412.authtemplate

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "WebSocket API", description = "WebSocket 메시지 전송 경로 설명")
@RestController
@RequestMapping("/ws-info")
class WebSocketInfoController {

    @Operation(summary = "채팅방 입장 경로", description = "채팅방에 입장할 때 사용하는 WebSocket 메시지 경로")
    @GetMapping("/enter")
    fun enterRoomInfo(): Map<String, String> {
        return mapOf(
            "messageMapping" to "/app/enter/roomId",
            "sendTo" to "/topic/room/roomId",
            "description" to "이 경로로 입장 메시지를 전송합니다.",
        )
    }

    @Operation(summary = "채팅방 퇴장 경로", description = "채팅방에서 퇴장할 때 사용하는 WebSocket 메시지 경로")
    @GetMapping("/exit")
    fun exitRoomInfo(): Map<String, String> {
        return mapOf(
            "messageMapping" to "/app/exit/roomId",
            "sendTo" to "/topic/room/roomId",
            "description" to "이 경로로 퇴장 메시지를 전송합니다.",
        )
    }

    @Operation(summary = "채팅 메시지 전송 경로", description = "특정 채팅방에 메시지를 보낼 때 사용하는 WebSocket 메시지 경로")
    @GetMapping("/chat")
    fun chatMessageInfo(): Map<String, String> {
        return mapOf(
            "messageMapping" to "/app/chat/roomId",
            "sendTo" to "/topic/room/roomId",
            "description" to "채팅 메시지를 이 경로로 보냅니다."
        )
    }
}