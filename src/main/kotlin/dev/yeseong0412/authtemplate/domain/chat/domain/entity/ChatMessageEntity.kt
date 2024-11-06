package dev.yeseong0412.authtemplate.domain.chat.domain.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "chat")
class ChatMessageEntity(
    @Id
    val id: ObjectId,
    var roomId: Long,
    var writerId: Long,
    var content: String
)