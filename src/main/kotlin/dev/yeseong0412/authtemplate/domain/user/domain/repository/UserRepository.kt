package dev.yeseong0412.authtemplate.domain.user.domain.repository

import dev.yeseong0412.authtemplate.domain.user.domain.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?

    fun findAllByEmail(email: String): List<UserEntity>
    fun existsByEmail(email: String): Boolean
}