package dev.yeseong0412.authtemplate.domain.chat.service

import dev.yeseong0412.authtemplate.domain.chat.domain.ChatRoomRepository
import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatRoomEntity
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
    private val jwtUtils: JwtUtils
) : ChatRoomService {
    override fun getAllRooms(): MutableList<ChatRoomEntity> = chatRoomRepository.findAll()

    override fun createRoom(name: String, userId: Long): BaseResponse<ChatRoomEntity> {

        val user = userRepository.findById(userId).get()

        val room = ChatRoomEntity(name = name, participants = mutableSetOf(user.name))
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
            data = room
        )
    }

    override fun deleteRoom(roomId: Long): BaseResponse<Unit> {
        chatRoomRepository.deleteById(roomId)

        return BaseResponse(
            message = "success"
        )
    }

    override fun enterRoom(roomId: Long, token: String): String {
        val username = jwtUtils.getUsername(token)
        return "$username 님이 $roomId 방에 입장하셨습니다."
    }

    override fun exitRoom(roomId: Long, token: String): String {
        val room = chatRoomRepository.findById(roomId).orElseThrow()
        val username = jwtUtils.getUsername(token)
        room.participants.remove(username)
        chatRoomRepository.save(room)

        return "$username 님이 $roomId 방에서 퇴장하셨습니다."
    }
}
