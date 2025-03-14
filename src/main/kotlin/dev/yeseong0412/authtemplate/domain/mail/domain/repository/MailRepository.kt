package dev.yeseong0412.authtemplate.domain.mail.domain.repository

interface MailRepository {
    fun save(email: String, authCode: String)
    fun findByEmail(email: String): String?
    fun deleteByEmail(email: String)
    fun existsByEmail(email: String): Boolean
}