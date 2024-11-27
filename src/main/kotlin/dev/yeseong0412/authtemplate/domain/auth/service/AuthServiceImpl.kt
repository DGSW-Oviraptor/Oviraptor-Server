package dev.yeseong0412.authtemplate.domain.auth.service

import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request.LoginRequest
import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request.RefreshRequest
import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.request.RegisterUserRequest
import dev.yeseong0412.authtemplate.domain.user.domain.entity.UserEntity
import dev.yeseong0412.authtemplate.domain.user.domain.mapper.UserMapper
import dev.yeseong0412.authtemplate.domain.mail.domain.repository.MailRepository
import dev.yeseong0412.authtemplate.domain.user.domain.repository.UserRepository
import dev.yeseong0412.authtemplate.domain.auth.exception.EmailErrorCode
import dev.yeseong0412.authtemplate.domain.auth.presentation.dto.response.LoginResponse
import dev.yeseong0412.authtemplate.domain.user.exception.UserErrorCode
import dev.yeseong0412.authtemplate.global.security.jwt.exception.JwtErrorCode
import dev.yeseong0412.authtemplate.global.security.jwt.exception.type.JwtErrorType
import dev.yeseong0412.authtemplate.global.security.jwt.util.JwtUtils
import dev.yeseong0412.authtemplate.global.common.BaseResponse
import dev.yeseong0412.authtemplate.global.exception.CustomException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthServiceImpl(
    private val userMapper: UserMapper,
    private val userRepository: UserRepository,
    private val mailRepository: MailRepository,
    private val jwtUtils: JwtUtils,
    private val bytePasswordEncoder: BCryptPasswordEncoder
) : AuthService {

    @Transactional
    override fun registerUser(registerUserRequest: RegisterUserRequest): BaseResponse<Unit> {
        if (userRepository.existsByEmail(registerUserRequest.email)) {
            throw CustomException(UserErrorCode.USER_ALREADY_EXIST)
        }

        if (mailRepository.findByEmail(registerUserRequest.email).isNullOrEmpty() || mailRepository.findByEmail(
                registerUserRequest.email
            ) != registerUserRequest.authCode
        ) {
            throw CustomException(EmailErrorCode.AUTHENTICODE_INVALID)
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
    override fun loginUser(loginRequest: LoginRequest): BaseResponse<LoginResponse> {
        val user = userRepository.findByEmail(loginRequest.email) ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        if (!bytePasswordEncoder.matches(loginRequest.password, user.password)) throw CustomException(UserErrorCode.USER_NOT_MATCH)

        val token = jwtUtils.generate(
            user = userMapper.toDomain(user)
        )

        return BaseResponse(
            message = "로그인 성공",
            data = LoginResponse(
                token.accessToken,
                token.refreshToken,
                user.name
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
}