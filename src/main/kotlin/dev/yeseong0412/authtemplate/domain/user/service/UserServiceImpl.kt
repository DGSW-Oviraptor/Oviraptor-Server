package dev.yeseong0412.authtemplate.domain.user.service

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

        if(userRepository.existsByEmail(registerUserRequest.email)) throw CustomException(UserErrorCode.USER_ALREADY_EXIST)

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

    override fun getUserInfo(userId: Long): BaseResponse<UserInfo> {
        val user = userRepository.findById(userId).orElseThrow()
        val userInfo = UserInfo(id = user.id, email = user.email, username = user.name)

        return BaseResponse(
            message = "success",
            data = userInfo
        )
    }

    override fun changeUserInfo(userId: Long, changeInfoRequest: ChangeInfoRequest): BaseResponse<UserEntity> {
        val user = userRepository.findById(userId).orElseThrow()
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
            data = UserEntity(id = user.id, email = user.email, name = user.name, password = changeInfoRequest.password, role = user.role)
        )
    }
}