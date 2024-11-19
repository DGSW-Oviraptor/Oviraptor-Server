package dev.yeseong0412.authtemplate.domain.chat.service

import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatMessageEntity
import dev.yeseong0412.authtemplate.domain.chat.domain.repository.ChatRoomRepository
import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatRoomEntity
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatMessageInfo
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatRoomInfo
import dev.yeseong0412.authtemplate.domain.chat.domain.repository.ChatMessageRepository
import dev.yeseong0412.authtemplate.domain.chat.exception.ChatRoomErrorCode
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.request.ChatMessage
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatOnline
import dev.yeseong0412.authtemplate.domain.user.domain.repository.UserRepository
import dev.yeseong0412.authtemplate.domain.user.exception.UserErrorCode
import dev.yeseong0412.authtemplate.global.auth.jwt.JwtUtils
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.exception.CustomException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ChatRoomServiceImpl(
    private val chatRoomRepository: ChatRoomRepository,
    private val userRepository: UserRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val jwtUtils: JwtUtils,

) : ChatRoomService {
    override fun getAllRooms(): BaseResponse<List<ChatRoomInfo>> {
        val rooms = chatRoomRepository.findAll()
        return BaseResponse(
            message = "success",
            data = rooms.map { ChatRoomInfo(it.id, it.name, it.participants.map { pr -> pr.name }) }
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
            data = ChatRoomInfo(id = room.id, name = room.name, participants = room.participants.map { it -> it.name })
        )
    }

    override fun inviteToRoom(roomId: Long, userEmail: String): BaseResponse<ChatRoomInfo> {
        val room = chatRoomRepository.findById(roomId).orElseThrow { CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND) }
        val user = userRepository.findByEmail(userEmail)?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        if (room.participants.size >= 8) throw CustomException(ChatRoomErrorCode.CHAT_ROOM_NUMBER_LIMIT_EXCEEDED)

        room.participants.add(user)
        user.rooms.add(room)
        chatRoomRepository.save(room)
        userRepository.save(user)

        return BaseResponse(
            message = "success",
            data = ChatRoomInfo(id = room.id, name = room.name, participants = room.participants.map { it -> it.name })
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

    override fun getRoomInfo(roomId: Long): BaseResponse<ChatRoomInfo> {
        val roomInfo = chatRoomRepository.findById(roomId).map { ChatRoomInfo(id = it.id, name = it.name, participants = it.participants.map { pr -> pr.name }) }.orElseThrow { CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND) }

        return BaseResponse(
            message = "success",
            data = roomInfo
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
        val messages = chatMessageRepository.findAllByRoomId(roomId)

        val chatMessage = ChatMessageEntity(chatId = messages.count().toLong(), roomId = roomId, writerId = user!!.id!!, content = message.message)
        chatMessageRepository.save(chatMessage)

        return ChatOnline(writer = user.name, message = message.message)
    }

    override fun getAllMessages(roomId: Long, userId: Long): BaseResponse<List<ChatMessageInfo>> {
        val room = chatRoomRepository.findById(roomId).orElseThrow { CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND) }
        val messages = chatMessageRepository.findAllByRoomId(roomId).map { ChatMessageInfo(chatId = it.chatId, room = room.name, writer = userRepository.findById(it.writerId).orElseThrow().name, content = it.content, isMine = it.writerId == userId) }

        return BaseResponse(
            message = "success",
            data = messages
        )
    }
}
