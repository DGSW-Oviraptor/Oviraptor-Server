package dev.yeseong0412.authtemplate.domain.chat.domain.repository

import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatMessageEntity
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ChatMessageRepository : MongoRepository<ChatMessageEntity, ObjectId> {
    @Query("{ 'roomId': ?0, '_id': { \$lt: ?1 } }")
    fun findMessagesByRoomIdAndObjectId(roomId: Long, objectId: ObjectId, pageable: Pageable): List<ChatMessageEntity>
    fun findFirstByRoomIdOrderByIdDesc(roomId: Long): ChatMessageEntity?

}