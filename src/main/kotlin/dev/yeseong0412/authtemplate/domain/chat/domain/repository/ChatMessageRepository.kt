package dev.yeseong0412.authtemplate.domain.chat.domain.repository

import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatMessageEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface ChatMessageRepository : MongoRepository<ChatMessageEntity, Long> {
    fun findAllByRoomId(roomId: Long): List<ChatMessageEntity>
}