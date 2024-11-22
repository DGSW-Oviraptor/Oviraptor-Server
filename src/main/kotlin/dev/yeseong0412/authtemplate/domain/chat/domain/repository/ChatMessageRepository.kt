package dev.yeseong0412.authtemplate.domain.chat.domain.repository

import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatMessageEntity
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ChatMessageRepository : MongoRepository<ChatMessageEntity, ObjectId> {
    @Query("{ 'roomId': ?0, '_id': { \$lte: ?1 } }")
    fun findMessagesByRoomIdAndTimestamp(roomId: Long, lastId: ObjectId, pageable: Pageable): List<ChatMessageEntity>
}