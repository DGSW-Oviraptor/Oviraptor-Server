package dev.yeseong0412.authtemplate.domain.chat.domain.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat")
class ChatMessageEntity(
    @Id
    val id: ObjectId? = null,
    var roomId: Long,
    var writerId: Long,
    var content: String,
    var timestamp: LocalDateTime = LocalDateTime.now(),
)