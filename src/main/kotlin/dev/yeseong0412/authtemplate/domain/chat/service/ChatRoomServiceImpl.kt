package dev.yeseong0412.authtemplate.domain.chat.service

import dev.yeseong0412.authtemplate.domain.chat.domain.ChatRoomRepository
import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatRoomEntity
import dev.yeseong0412.authtemplate.domain.user.domain.UserRepository
import dev.yeseong0412.authtemplate.domain.user.exception.UserErrorCode
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.exception.CustomException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class ChatRoomServiceImpl(
    private val chatRoomRepository: ChatRoomRepository,
    private val userRepository: UserRepository
) : ChatRoomService {
    override fun getAllRooms(): MutableList<ChatRoomEntity> = chatRoomRepository.findAll()

    override fun createRoom(name: String): BaseResponse<ChatRoomEntity> {

        val authentication = SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken

        val room = ChatRoomEntity(name = name, participants = mutableListOf(authentication.name))
        chatRoomRepository.save(room)

        return BaseResponse(
            message = "success",
            data = room
        )
    }

    override fun inviteToRoom(roomId: Long, userEmail: String): BaseResponse<ChatRoomEntity> {
        val room = chatRoomRepository.findById(roomId).orElseThrow()
        val user = userRepository.findByEmail(userEmail)?: throw CustomException(UserErrorCode.USER_NOT_FOUND)
        room.participants.add(user.name)
        chatRoomRepository.save(room)

        return BaseResponse(
            message = "success",
            data = ChatRoomEntity(name = room.name, participants = room.participants)
        )
    }

    override fun deleteRoom(roomId: Long): BaseResponse<Unit> {
        chatRoomRepository.deleteById(roomId)

        return BaseResponse(
            message = "success"
        )
    }

    override fun enterRoom(roomId: Long, username: String): String {
        return "$username 님이 $roomId 방에 입장하셨습니다."
    }

    override fun exitRoom(roomId: Long, username: String): String {
        val room = chatRoomRepository.findById(roomId).orElseThrow()
        room.participants.remove(username)
        chatRoomRepository.save(room)

        return "$username 님이 $roomId 방에서 퇴장하셨습니다."
    }
}
