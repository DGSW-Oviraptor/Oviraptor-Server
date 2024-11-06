package dev.yeseong0412.authtemplate.domain.chat.service

import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatMessageEntity
import dev.yeseong0412.authtemplate.domain.chat.domain.repository.ChatRoomRepository
import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatRoomEntity
import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoomIdInfo
import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoomInfo
import dev.yeseong0412.authtemplate.domain.chat.domain.repository.ChatMessageRepository
import dev.yeseong0412.authtemplate.domain.chat.exception.ChatRoomErrorCode
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.ChatMessage
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.ChatOnline
import dev.yeseong0412.authtemplate.domain.user.domain.repository.UserRepository
import dev.yeseong0412.authtemplate.domain.user.exception.UserErrorCode
import dev.yeseong0412.authtemplate.global.auth.jwt.JwtUtils
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.exception.CustomException
import org.springframework.stereotype.Service

@Service
class ChatRoomServiceImpl(
    private val chatRoomRepository: ChatRoomRepository,
    private val userRepository: UserRepository,
    private val chatMessageRepository: ChatMessageRepository,
    val jwtUtils: JwtUtils
) : ChatRoomService {
    override fun getAllRooms(): BaseResponse<List<ChatRoomIdInfo>> {
        val rooms = chatRoomRepository.findAll()
        return BaseResponse(
            message = "success",
            data = rooms.map { ChatRoomIdInfo(it.id, it.name, it.participants.map { pr -> pr.name }) }
        )
    }


    override fun createRoom(name: String, userId: Long): BaseResponse<ChatRoomInfo> {
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }
        val room = ChatRoomEntity(name = name, participants = mutableSetOf(user))

        chatRoomRepository.save(room)
        user.rooms.add(room)
        userRepository.save(user)

        return BaseResponse(
            message = "success",
            data = ChatRoomInfo(name = room.name, participants = room.participants.map { it -> it.name })
        )
    }

    override fun inviteToRoom(roomId: Long, userEmail: String): BaseResponse<ChatRoomInfo> {
        val room = chatRoomRepository.findById(roomId).orElseThrow { CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND) }
        val user = userRepository.findByEmail(userEmail) ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        if (room.participants.size >= 8) throw CustomException(ChatRoomErrorCode.CHAT_ROOM_NUMBER_LIMIT_EXCEEDED)

        room.participants.add(user)
        user.rooms.add(room)
        chatRoomRepository.save(room)
        userRepository.save(user)

        return BaseResponse(
            message = "success",
            data = ChatRoomInfo(name = room.name, participants = room.participants.map { it -> it.name })
        )
    }

    override fun deleteRoom(roomId: Long): BaseResponse<Unit> {
        val room = chatRoomRepository.findById(roomId).orElseThrow { CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND) }

        room.participants.forEach { user -> user.rooms.remove(room) }

        chatRoomRepository.delete(room)

        return BaseResponse(
            message = "success"
        )
    }

    override fun enterRoom(roomId: Long, userId: Long): ChatOnline {
        val room = chatRoomRepository.findById(roomId).orElseThrow { CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND) }
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }

        return ChatOnline(writer = "시스템", message = "${user.name} 님이 ${room.name}에 입장하셨습니다.")
    }

    override fun exitRoom(roomId: Long, userId: Long): ChatOnline {
        val room = chatRoomRepository.findById(roomId).orElseThrow { CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND) }
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }

        room.participants.remove(user)
        chatRoomRepository.save(room)

        return ChatOnline(writer = "시스템", message = "${user.name} 님이 ${room.name}에서 퇴장하셨습니다.")
    }

    override fun sendChat(roomId: Long, token: String, message: ChatMessage): ChatOnline {
        val user = userRepository.findByEmail(jwtUtils.getUsername(token))
        chatMessageRepository.save(ChatMessageEntity(roomId = roomId, writerId = user!!.id, content = message.message))

        return ChatOnline(writer = user.name, message = message.message)
    }

    override fun getAllMessages(roomId: Long): BaseResponse<List<ChatMessageEntity>> {
        return BaseResponse(
            message = "success",
            data = chatMessageRepository.findAllByRoomId(roomId)
        )
    }
}
