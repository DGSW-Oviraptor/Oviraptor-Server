package dev.yeseong0412.authtemplate.domain.chat.exception

import dev.yeseong0412.authtemplate.global.exception.CustomErrorCode
import org.springframework.http.HttpStatus

enum class ChatRoomErrorCode(
    override val status: HttpStatus,
    override val state: String,
    override val message: String,
) : CustomErrorCode {

    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "방을 찾을 수 없습니다"),
    CHATROOM_ALREADY_EXIST(HttpStatus.CONFLICT, "CONFLICT", "방이 이미 존재합니다")
}