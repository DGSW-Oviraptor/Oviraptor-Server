package dev.yeseong0412.authtemplate.domain.user.domain.repository

import dev.yeseong0412.authtemplate.domain.user.domain.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
    fun findAllByNameContaining(name: String) : List<UserEntity>
    fun existsByEmail(email: String): Boolean
}