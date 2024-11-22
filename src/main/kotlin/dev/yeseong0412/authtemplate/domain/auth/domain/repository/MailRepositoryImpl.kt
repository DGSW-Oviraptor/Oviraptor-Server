package dev.yeseong0412.authtemplate.domain.auth.domain.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class MailRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, String>
) : MailRepository {

    override fun save(email: String, authCode: String) {
        redisTemplate.opsForValue().set(email, authCode, 180, TimeUnit.SECONDS)
    }

    override fun findByEmail(email: String): String? {
        return redisTemplate.opsForValue().get(email)
    }

    override fun deleteByEmail(email: String) {
        redisTemplate.delete(email)
    }

    override fun existsByEmail(email: String): Boolean {
        return redisTemplate.hasKey(email)
    }
}