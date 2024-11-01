package dev.yeseong0412.authtemplate.domain.chat.domain.entity

import dev.yeseong0412.authtemplate.domain.user.domain.entity.UserEntity
import jakarta.persistence.*

@Entity(name = "chatroom")
class ChatRoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null, // ID (PK)

    @Column(nullable = false)
    val name: String, // Email

    @ManyToMany
    val participants: MutableSet<UserEntity> = mutableSetOf()
)