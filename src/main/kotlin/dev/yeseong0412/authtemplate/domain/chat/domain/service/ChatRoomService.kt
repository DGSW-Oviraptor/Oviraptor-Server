package dev.yeseong0412.authtemplate.domain.chat.domain.service

import dev.yeseong0412.authtemplate.domain.chat.domain.ChatRoomRepository
import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatRoomEntity
import dev.yeseong0412.authtemplate.domain.chat.domain.mapper.ChatRoomMapper
import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoom
import dev.yeseong0412.authtemplate.domain.user.domain.UserRepository
import dev.yeseong0412.authtemplate.domain.user.exception.UserErrorCode
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.exception.CustomException
import org.springframework.stereotype.Service

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val userRepository: UserRepository,
    private val chatRoomMapper: ChatRoomMapper
) {
    fun getAllRooms(): MutableList<ChatRoomEntity> = chatRoomRepository.findAll()

    fun createRoom(name: String): BaseResponse<ChatRoom> {
        val room = ChatRoom(name = name)
        chatRoomRepository.save(chatRoomMapper.toEntity(room))
        return BaseResponse(
            message = "success",
            data = room
        )
    }

    fun inviteToRoom(roomId: Long, userEmail: String): BaseResponse<ChatRoom> {
        val room = chatRoomRepository.findById(roomId).orElseThrow()
        val user = userRepository.findByEmail(userEmail)?: throw CustomException(UserErrorCode.USER_NOT_FOUND)
        room.participants.add(user.email)
        chatRoomRepository.save(room)

        return BaseResponse(
            message = "success",
            data = ChatRoom(name = room.name, participants = room.participants)
        )
    }

    fun deleteRoom(roomId: Long): BaseResponse<Unit> {
        chatRoomRepository.deleteById(roomId)

        return BaseResponse(
            message = "success"
        )
    }

    fun enterRoom(roomId: Long, username: String): String {
        return "$username 님이 $roomId 방에 입장하셨습니다."
    }

    fun exitRoom(roomId: Long, username: String): String {
        val room = chatRoomRepository.findById(roomId).orElseThrow()
        room.participants.remove(username)
        chatRoomRepository.save(room)

        return "$username 님이 $roomId 방에서 퇴장하셨습니다."
    }
}
