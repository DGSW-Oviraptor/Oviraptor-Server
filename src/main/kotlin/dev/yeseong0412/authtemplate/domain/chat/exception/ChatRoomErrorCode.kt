package dev.yeseong0412.authtemplate.domain.chat.exception

import dev.yeseong0412.authtemplate.global.exception.CustomErrorCode
import org.springframework.http.HttpStatus

enum class ChatRoomErrorCode(
    override val status: HttpStatus,
    override val state: String,
    override val message: String,
) : CustomErrorCode {
    CHAT_ROOM_NUMBER_LIMIT_EXCEEDED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "방의 최대 인원을 초과했습니다.")
}