package dev.yeseong0412.authtemplate.domain.user.service

import dev.yeseong0412.authtemplate.domain.auth.exception.EmailErrorCode
import dev.yeseong0412.authtemplate.domain.chat.domain.repository.ChatMessageRepository
import dev.yeseong0412.authtemplate.domain.chat.domain.repository.ChatRoomRepository
import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatRoom
import dev.yeseong0412.authtemplate.domain.mail.domain.repository.MailRepository
import dev.yeseong0412.authtemplate.domain.user.domain.entity.UserEntity
import dev.yeseong0412.authtemplate.domain.user.domain.mapper.UserMapper
import dev.yeseong0412.authtemplate.domain.user.domain.repository.UserRepository
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.response.UserInfo
import dev.yeseong0412.authtemplate.domain.user.exception.UserErrorCode
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.*
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.exception.CustomException
import dev.yeseong0412.authtemplate.global.security.jwt.dto.JwtInfo
import dev.yeseong0412.authtemplate.global.security.jwt.util.JwtUtils
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val mailRepository: MailRepository,
    private val bytePasswordEncoder: BCryptPasswordEncoder,
    private val chatMessageRepository: ChatMessageRepository,
    private val jwtUtils: JwtUtils,
    private val userMapper: UserMapper,
    private val chatRoomRepository: ChatRoomRepository
) : UserService {

    @Transactional(readOnly = true)
    override fun getMyRooms(userId: Long): BaseResponse<List<ChatRoom>> {
        val user = getUser(userId)
        val rooms = user.rooms

        return BaseResponse(
            message = "success",
            data = rooms.map {
                ChatRoom(
                    id = it.id,
                    name = it.name,
                    participants = it.participants.map { pr -> pr.name },
                    lastMessage = chatMessageRepository.findFirstByRoomIdOrderByIdDesc(it.id ?: 0)?.content ?: ""
                )
            }
        )
    }

    @Transactional(readOnly = true)
    override fun getUserInfo(userId: Long): BaseResponse<UserInfo> {
        val user = getUser(userId)
        val userInfo = UserInfo(email = user.email, name = user.name, role = user.role)

        return BaseResponse(
            message = "success",
            data = userInfo
        )
    }

    @Transactional
    override fun changeUsername(userId: Long, username: String): BaseResponse<Unit> {
        val user = getUser(userId)

        if (username.isNotBlank()) {
            user.name = username.trim()
            userRepository.save(user)
        }

        return BaseResponse(
            message = "success"
        )
    }

    @Transactional
    override fun changeEmail(userId: Long, request: ChangeEmailRequest): BaseResponse<JwtInfo> {
        val user = getUser(userId)

        if (userRepository.existsByEmail(request.email)) {
            throw CustomException(UserErrorCode.USER_ALREADY_EXIST)
        }

        if (mailRepository.findByEmail(request.email).isNullOrEmpty() || mailRepository.findByEmail(
                request.email
            ) != request.authCode
        ) {
            throw CustomException(EmailErrorCode.AUTHENTICODE_INVALID)
        }

        user.email = request.email
        userRepository.save(user)
        mailRepository.deleteByEmail(request.email)

        return BaseResponse(
            message = "success",
            data = jwtUtils.generate(
                user = userMapper.toDomain(user)
            )
        )
    }

    @Transactional
    override fun changePassword(userId: Long, changePasswordRequest: ChangePasswordRequest): BaseResponse<Unit> {
        val user = getUser(userId)

        if (!bytePasswordEncoder.matches(
                changePasswordRequest.oldPassword,
                user.password
            )
        ) throw CustomException(UserErrorCode.PASSWORD_NOT_MATCH)

        if (changePasswordRequest.newPassword.isBlank()) throw CustomException(UserErrorCode.PASSWORD_INVALID)

        user.password = bytePasswordEncoder.encode(changePasswordRequest.newPassword.trim())
        userRepository.save(user)

        return BaseResponse(
            message = "success"
        )
    }

    @Transactional
    override fun addFriend(userId: Long, email: String): BaseResponse<Unit> {
        val user = getUser(userId)
        val friend = userRepository.findByEmail(email) ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)
        if (user == friend || user.friends.contains(friend)) {
            throw CustomException(UserErrorCode.CANNOT_ADD_FRIEND)
        }

        user.friends.add(friend)
        friend.friends.add(user)
        userRepository.save(user)
        userRepository.save(friend)

        return BaseResponse(
            message = "success"
        )
    }

    @Transactional(readOnly = true)
    override fun getAllFriends(userId: Long): BaseResponse<List<UserInfo>> {
        val user = getUser(userId)
        val friends = user.friends

        return BaseResponse(
            message = "success",
            data = friends.map { UserInfo(email = it.email, name = it.name, role = it.role) }
        )
    }

    @Transactional
    override fun deleteFriend(userId: Long, email: String): BaseResponse<Unit> {
        val user = getUser(userId)
        val friend = userRepository.findByEmail(email) ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        user.friends.remove(friend)
        friend.friends.remove(user)

        userRepository.save(user)
        userRepository.save(friend)

        return BaseResponse(
            message = "success"
        )
    }

    @Transactional(readOnly = true)
    override fun searchByUsername(username: String): BaseResponse<List<UserInfo>> {
        val user = userRepository.findAllByNameContaining(username)
        return BaseResponse(
            message = "success",
            data = user.map { UserInfo(email = it.email, name = it.name, role = it.role) }
        )
    }

    @Transactional
    override fun deleteUser(userId: Long, deleteUserRequest: DeleteUserRequest): BaseResponse<Unit> {
        val user = getUser(userId)

        if (!bytePasswordEncoder.matches(
                deleteUserRequest.password,
                user.password
            )
        ) throw CustomException(UserErrorCode.PASSWORD_NOT_MATCH)

        user.rooms.map {
            it.participants.remove(user)
            if (it.participants.isEmpty()) chatRoomRepository.delete(it)
        }
        user.friends.map { it.friends.remove(user) }
        userRepository.delete(user)

        return BaseResponse(
            message = "success"
        )
    }

    fun getUser(userId: Long): UserEntity = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }
}