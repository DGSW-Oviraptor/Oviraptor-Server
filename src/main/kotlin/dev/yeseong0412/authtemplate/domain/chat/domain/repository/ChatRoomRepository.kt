package dev.yeseong0412.authtemplate.domain.chat.domain.repository

import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatRoomEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomRepository : JpaRepository<ChatRoomEntity, Long> {
}