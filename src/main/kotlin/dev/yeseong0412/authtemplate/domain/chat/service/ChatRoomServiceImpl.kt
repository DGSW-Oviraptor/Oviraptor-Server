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
import dev.yeseong0412.authtemplate.global.security.jwt.util.JwtUtils
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.exception.CustomException
import org.bson.types.ObjectId
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomServiceImpl(
    private val chatRoomRepository: ChatRoomRepository,
    private val userRepository: UserRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val jwtUtils: JwtUtils
) : ChatRoomService {

    @Transactional(readOnly = true)
    override fun getAllRooms(): BaseResponse<List<ChatRoomInfo>> {
        val rooms = chatRoomRepository.findAll()
        return BaseResponse(
            message = "success",
            data = rooms.map { ChatRoomInfo(it.id, it.name, it.participants.map { pr -> pr.name }) }
        )
    }

    @Transactional
    override fun createRoom(name: String, userId: Long): BaseResponse<Unit> {
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }
        val room = ChatRoomEntity(name = name, participants = mutableSetOf(user))

        chatRoomRepository.save(room)
        user.rooms.add(room)
        userRepository.save(user)

        return BaseResponse(
            message = "success"
        )
    }

    @Transactional
    override fun inviteToRoom(roomId: Long, userEmail: String): BaseResponse<Unit> {
        val room = chatRoomRepository.findById(roomId).orElseThrow { CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND) }
        val user = userRepository.findByEmail(userEmail) ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        if (room.participants.size >= 8) throw CustomException(ChatRoomErrorCode.CHAT_ROOM_NUMBER_LIMIT_EXCEEDED)

        room.participants.add(user)
        user.rooms.add(room)
        chatRoomRepository.save(room)
        userRepository.save(user)

        return BaseResponse(
            message = "success"
        )
    }

    @Transactional
    override fun deleteRoom(roomId: Long): BaseResponse<Unit> {
        val room = chatRoomRepository.findById(roomId).orElseThrow { CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND) }

        room.participants.forEach { user -> user.rooms.remove(room) }

        chatRoomRepository.delete(room)

        return BaseResponse(
            message = "success"
        )
    }

    @Transactional(readOnly = true)
    override fun getRoomInfo(roomId: Long): BaseResponse<ChatRoomInfo> {
        val roomInfo = chatRoomRepository.findById(roomId)
            .map { ChatRoomInfo(id = it.id, name = it.name, participants = it.participants.map { pr -> pr.name }) }
            .orElseThrow { CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND) }

        return BaseResponse(
            message = "success",
            data = roomInfo
        )
    }

    @Transactional(readOnly = true)
    override fun enterRoom(roomId: Long, userId: Long): ChatOnline {
        val room = chatRoomRepository.findById(roomId).orElseThrow { CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND) }
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }

        return ChatOnline(writer = "시스템", message = "${user.name} 님이 ${room.name}에 입장하셨습니다.")
    }

    @Transactional
    override fun exitRoom(roomId: Long, userId: Long): ChatOnline {
        val room = chatRoomRepository.findById(roomId).orElseThrow { CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND) }
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }

        room.participants.remove(user)
        chatRoomRepository.save(room)

        if (room.participants.size == 0) {
            chatRoomRepository.delete(room)
        }

        return ChatOnline(writer = "시스템", message = "${user.name} 님이 ${room.name}에서 퇴장하셨습니다.")
    }

    @Transactional
    override fun sendChat(roomId: Long, token: String, message: ChatMessage): ChatOnline {
        val user = userRepository.findByEmail(jwtUtils.getUsername(token))

        val chatMessage = ChatMessageEntity(roomId = roomId, writerId = user!!.id!!, content = message.message)
        chatMessageRepository.save(chatMessage)

        return ChatOnline(writer = user.name, message = message.message)
    }

    @Transactional(readOnly = true)
    override fun getAllMessages(roomId: Long, objectId: String?, userId: Long): BaseResponse<List<ChatMessageInfo>> {
        val room = chatRoomRepository.findById(roomId).orElseThrow { CustomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND) }
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }
        val id = if (objectId != null) ObjectId(objectId) else ObjectId()
        val pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "_id"))
        val messages = chatMessageRepository.findMessagesByRoomIdAndObjectId(roomId, id, pageable)
            .map {
                ChatMessageInfo(
                    id = it.id.toString(),
                    room = room.name,
                    writer = user.name,
                    content = it.content,
                    isMine = it.writerId == userId,
                )
            }

        return BaseResponse(
            message = "success",
            data = messages
        )
    }
}
