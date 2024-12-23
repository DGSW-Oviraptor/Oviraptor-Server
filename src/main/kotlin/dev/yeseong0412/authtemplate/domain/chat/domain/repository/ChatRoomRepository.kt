package dev.yeseong0412.authtemplate.domain.chat.domain.repository

import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatRoomEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRepository : JpaRepository<ChatRoomEntity, Long> {
    @Query("""
    SELECT c 
    FROM chatroom c 
    WHERE EXISTS (
        SELECT 1 
        FROM c.participants p 
        WHERE p.id = ?1
    )
    ORDER BY c.modifiedAt DESC
""")
    fun findMyRooms(userId: Long): List<ChatRoomEntity>
}