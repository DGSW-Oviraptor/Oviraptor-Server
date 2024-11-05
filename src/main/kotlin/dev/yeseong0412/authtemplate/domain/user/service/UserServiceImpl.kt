package dev.yeseong0412.authtemplate.domain.user.service

import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoomInfo
import dev.yeseong0412.authtemplate.domain.user.domain.entity.UserEntity
import dev.yeseong0412.authtemplate.domain.user.domain.repository.UserRepository
import dev.yeseong0412.authtemplate.domain.user.domain.mapper.UserMapper
import dev.yeseong0412.authtemplate.domain.user.domain.model.UserInfo
import dev.yeseong0412.authtemplate.domain.user.exception.UserErrorCode
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.ChangeInfoRequest
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.LoginRequest
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.RefreshRequest
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.RegisterUserRequest
import dev.yeseong0412.authtemplate.global.auth.jwt.JwtInfo
import dev.yeseong0412.authtemplate.global.auth.jwt.JwtUtils
import dev.yeseong0412.authtemplate.global.auth.jwt.exception.JwtErrorCode
import dev.yeseong0412.authtemplate.global.auth.jwt.exception.type.JwtErrorType
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.exception.CustomException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val bytePasswordEncoder: BCryptPasswordEncoder,
    private val jwtUtils: JwtUtils
) : UserService {

    @Transactional
    override fun registerUser(registerUserRequest: RegisterUserRequest): BaseResponse<Unit> {
        if (userRepository.existsByEmail(registerUserRequest.email)) throw CustomException(UserErrorCode.USER_ALREADY_EXIST)

        userRepository.save(
            userMapper.toEntity(
                userMapper.toDomain(registerUserRequest, bytePasswordEncoder.encode(registerUserRequest.password.trim()))
            )
        )

        return BaseResponse(
            message = "회원가입 성공"
        )

    }

    @Transactional(readOnly = true)
    override fun loginUser(loginRequest: LoginRequest): BaseResponse<JwtInfo> {
        val user = userRepository.findByEmail(loginRequest.email)?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        if (bytePasswordEncoder.matches(user.password, loginRequest.password)) throw CustomException(UserErrorCode.USER_NOT_MATCH)

        return BaseResponse(
            message = "로그인 성공",
            data = jwtUtils.generate(
                user = userMapper.toDomain(user)
            )
        )

    }

    @Transactional(readOnly = true)
    override fun refreshToken(refreshRequest: RefreshRequest): BaseResponse<String> {
        val token = jwtUtils.getToken(refreshRequest.refreshToken)

        if (jwtUtils.checkTokenInfo(token) == JwtErrorType.ExpiredJwtException) {
            throw CustomException(JwtErrorCode.JWT_TOKEN_EXPIRED)
        }

        val user = userRepository.findByEmail(
            jwtUtils.getUsername(token)
        )

        return BaseResponse (
            message = "리프레시 성공 !",
            data = jwtUtils.refreshToken(
                user = userMapper.toDomain(user!!)
            )
        )
    }

    override fun getAllRooms(userId: Long): List<ChatRoomInfo> {
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }
        val rooms = user.rooms

        return rooms.map { ChatRoomInfo(name = it.name, participants = it.participants.map { pr -> pr.name}) }
    }

    override fun getUserInfo(userId: Long): BaseResponse<UserInfo> {
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }
        val userInfo = UserInfo(email = user.email, name = user.name)

        return BaseResponse(
            message = "success",
            data = userInfo
        )
    }

    override fun changeUserInfo(userId: Long, changeInfoRequest: ChangeInfoRequest): BaseResponse<UserInfo> {
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }
        if (userRepository.existsByEmail(changeInfoRequest.email) && user.email != changeInfoRequest.email) throw CustomException(UserErrorCode.USER_ALREADY_EXIST)

        if (changeInfoRequest.email != "") {
            user.email = changeInfoRequest.email
        }
        if (changeInfoRequest.name != "") {
            user.name = changeInfoRequest.name
        }
        if (changeInfoRequest.password != "") {
            user.password = bytePasswordEncoder.encode(changeInfoRequest.password.trim())
        }

        userRepository.save(user)

        return BaseResponse(
            message = "success",
            data = UserInfo(email = user.email, name = user.name)
        )
    }

    override fun addFriend(userId: Long, userEmail: String): BaseResponse<UserInfo> {
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }
        val friend = userRepository.findByEmail(userEmail)?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        if (user == friend) {
            throw CustomException(UserErrorCode.CANNOT_ADD_FRIEND)
        }

        user.friends.add(friend)
        friend.friends.add(user)
        userRepository.save(user)
        userRepository.save(friend)

        return BaseResponse(
            message = "success",
            data = UserInfo(email = userEmail, name = friend.name)
        )
    }

    override fun getAllFriends(userId: Long): List<UserInfo> {
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }
        val friends = user.friends

        return friends.map { UserInfo(email = it.email, name = it.name) }
    }

    override fun searchByUserName(userName: String): List<UserInfo> {
        val user = userRepository.findAllByNameContaining(userName)
        return user.map { UserInfo(email = it.email, name = it.name) }
    }

}