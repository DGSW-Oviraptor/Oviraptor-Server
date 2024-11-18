package dev.yeseong0412.authtemplate.domain.user.service

import dev.yeseong0412.authtemplate.domain.chat.presentation.dto.response.ChatRoomInfo
import dev.yeseong0412.authtemplate.domain.user.domain.entity.UserEntity
import dev.yeseong0412.authtemplate.domain.user.exception.EmailErrorCode
import dev.yeseong0412.authtemplate.domain.user.domain.repository.UserRepository
import dev.yeseong0412.authtemplate.domain.user.domain.mapper.UserMapper
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.response.UserInfo
import dev.yeseong0412.authtemplate.domain.user.domain.repository.MailRepository
import dev.yeseong0412.authtemplate.domain.user.exception.UserErrorCode
import dev.yeseong0412.authtemplate.domain.user.presentation.dto.request.*
import dev.yeseong0412.authtemplate.global.auth.jwt.JwtInfo
import dev.yeseong0412.authtemplate.global.auth.jwt.JwtUtils
import dev.yeseong0412.authtemplate.global.auth.mail.MailUtility
import dev.yeseong0412.authtemplate.global.auth.jwt.exception.JwtErrorCode
import dev.yeseong0412.authtemplate.global.auth.jwt.exception.type.JwtErrorType
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.exception.CustomException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val mailRepository: MailRepository,
    private val bytePasswordEncoder: BCryptPasswordEncoder,
    private val jwtUtils: JwtUtils,
    private val mailUtils: MailUtility
) : UserService {

    @Transactional
    override fun registerUser(registerUserRequest: RegisterUserRequest): BaseResponse<Unit> {
        if (mailRepository.findByEmail(registerUserRequest.email).isNullOrEmpty() || mailRepository.findByEmail(registerUserRequest.email) != registerUserRequest.authCode) {
            throw CustomException(EmailErrorCode.AUTHENTICODE_INVALID)
        }

        if (userRepository.existsByEmail(registerUserRequest.email)) {
            throw CustomException(UserErrorCode.USER_ALREADY_EXIST)
        }

        if (registerUserRequest.name.isEmpty()) {
            throw CustomException(UserErrorCode.USERNAME_INVALID)
        }

        if (registerUserRequest.password.isEmpty()) {
            throw CustomException(UserErrorCode.PASSWORD_INVALID)
        }

        val user = UserEntity(
            email = registerUserRequest.email,
            name = registerUserRequest.name,
            password = bytePasswordEncoder.encode(registerUserRequest.password)
        )
        userRepository.save(user)

        mailRepository.deleteByEmail(registerUserRequest.email)

        return BaseResponse(message = "success")
    }

    @Transactional(readOnly = true)
    override fun loginUser(loginRequest: LoginRequest): BaseResponse<JwtInfo> {
        val user = userRepository.findByEmail(loginRequest.email) ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        if (bytePasswordEncoder.matches(
                user.password,
                loginRequest.password
            )
        ) throw CustomException(UserErrorCode.USER_NOT_MATCH)

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

        return BaseResponse(
            message = "리프레시 성공 !",
            data = jwtUtils.refreshToken(
                user = userMapper.toDomain(user!!)
            )
        )
    }

    override fun getAllRooms(userId: Long): BaseResponse<List<ChatRoomInfo>> {
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }
        val rooms = user.rooms

        return BaseResponse(
            message = "success",
            data = rooms.map { ChatRoomInfo(id = it.id, name = it.name, participants = it.participants.map { pr -> pr.name }) }
        )
    }

    override fun getUserInfo(userId: Long): BaseResponse<UserInfo> {
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }
        val userInfo = UserInfo(email = user.email, name = user.name, role = user.role)

        return BaseResponse(
            message = "success",
            data = userInfo
        )
    }

    override fun changeUserInfo(userId: Long, changeInfoRequest: ChangeInfoRequest): BaseResponse<UserInfo> {
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }

        if (userRepository.existsByEmail(changeInfoRequest.email) && user.email != changeInfoRequest.email) throw CustomException(
            UserErrorCode.USER_ALREADY_EXIST
        )
        if (changeInfoRequest.email != "") {
            user.email = changeInfoRequest.email
        }
        if (changeInfoRequest.name != "") {
            user.name = changeInfoRequest.name
        }

        userRepository.save(user)

        return BaseResponse(
            message = "success",
            data = UserInfo(email = user.email, name = user.name, role = user.role)
        )
    }

    override fun changePassword(userId: Long, changePasswordRequest: ChangePasswordRequest): BaseResponse<Unit> {
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }

        if (bytePasswordEncoder.matches(
            user.password,
            changePasswordRequest.oldPassword
        )) throw CustomException(UserErrorCode.PASSWORD_NOT_MATCH)

        user.password = bytePasswordEncoder.encode(changePasswordRequest.newPassword.trim())

        userRepository.save(user)

        return BaseResponse(
            message = "success"
        )
    }

    override fun addFriend(userId: Long, email: String): BaseResponse<UserInfo> {
        val user = userRepository.findByIdOrNull(userId)?: throw  CustomException(UserErrorCode.USER_NOT_FOUND)
        val friend = userRepository.findByEmail(email)?: throw CustomException(UserErrorCode.USER_NOT_FOUND)
        if (user == friend || user.friends.contains(friend)) {
            throw CustomException(UserErrorCode.CANNOT_ADD_FRIEND)
        }

        user.friends.add(friend)
        friend.friends.add(user)
        userRepository.save(user)
        userRepository.save(friend)

        return BaseResponse(
            message = "success",
            data = UserInfo(email = friend.email, name = friend.name, role = user.role)
        )
    }

    override fun getAllFriends(userId: Long): BaseResponse<List<UserInfo>> {
        val user = userRepository.findById(userId).orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }
        val friends = user.friends

        return BaseResponse(
            message = "success",
            data = friends.map { UserInfo(email = it.email, name = it.name, role = it.role) }
        )
    }

    override fun searchByUsername(username: String): BaseResponse<List<UserInfo>> {
        val user = userRepository.findAllByNameContaining(username)
        return BaseResponse(
            message = "success",
            data = user.map { UserInfo(email = it.email, name = it.name, role = it.role) }
        )
    }

    override fun sendMail(email: String): BaseResponse<Unit> {

        if (!isValidEmail(email)) {
            throw CustomException(EmailErrorCode.EMAIL_INVALID)
        }
        val authCode = mailUtils.sendMail(email)

        mailRepository.save(email, authCode)

        return BaseResponse(
            message = "success"
        )
    }

    fun isValidEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex() // 이메일 형식 검증
        return email.matches(regex)
    }
}