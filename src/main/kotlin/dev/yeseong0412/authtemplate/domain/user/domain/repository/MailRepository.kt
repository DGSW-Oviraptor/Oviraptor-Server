package dev.yeseong0412.authtemplate.domain.user.domain.repository

import dev.yeseong0412.authtemplate.domain.user.domain.entity.MailEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MailRepository : JpaRepository<MailEntity, Long> {
    fun findAllByEmail(email:String) : List<MailEntity>
    fun deleteByEmail(email:String)
}