package dev.yeseong0412.authtemplate.global.security.jwt.details

import dev.yeseong0412.authtemplate.domain.user.domain.repository.UserRepository
import dev.yeseong0412.authtemplate.domain.user.domain.mapper.UserMapper
import dev.yeseong0412.authtemplate.domain.user.exception.UserErrorCode
import dev.yeseong0412.authtemplate.global.exception.CustomException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JwtUserDetailsService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(email: String): UserDetails {
        println("hihi")
        if (userRepository.existsByEmail(email)) {
            println("exists")
        }
        return JwtUserDetails(
            user = userMapper.toDomain(
                entity = userRepository.findByEmail(email) ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)
            )
        )
    }
}