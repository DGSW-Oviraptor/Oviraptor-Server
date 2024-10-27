package dev.yeseong0412.authtemplate.domain.chat.domain.mapper

import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatRoomEntity
import dev.yeseong0412.authtemplate.domain.chat.domain.model.ChatRoom
import dev.yeseong0412.authtemplate.global.common.Mapper
import org.springframework.stereotype.Component

@Component
class ChatRoomMapper(
) : Mapper<ChatRoom, ChatRoomEntity> {

    override fun toDomain(entity: ChatRoomEntity): ChatRoom {
        return ChatRoom(
            id = entity.id,
            name = entity.name,
            participants = entity.participants
        )
    }

    override fun toEntity(domain: ChatRoom): ChatRoomEntity {
        return ChatRoomEntity(
            id = domain.id,
            name = domain.name,
            participants = domain.participants
        )
    }
}