package dev.yeseong0412.authtemplate.domain.chat.exception

import dev.yeseong0412.authtemplate.global.exception.CustomErrorCode
import org.springframework.http.HttpStatus

enum class ChatRoomErrorCode(
    override val status: HttpStatus,
    override val state: String,
    override val message: String
) : CustomErrorCode {
    CHAT_ROOM_NUMBER_LIMIT_EXCEEDED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "방의 최대 인원을 초과했습니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "방을 찾을 수 없습니다."),
    CANNOT_DELETE_CHATROOM(HttpStatus.FORBIDDEN, "FORBIDDEN", "방을 삭제할 수 없습니다."),
    ROOM_NAME_INVALID(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "방 이름이 형식에 맞지 않습니다.")
}