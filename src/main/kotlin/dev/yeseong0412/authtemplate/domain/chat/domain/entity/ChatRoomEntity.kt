package dev.yeseong0412.authtemplate.domain.chat.domain.entity

import jakarta.persistence.*

@Entity(name = "chatroom")
class ChatRoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null, // ID (PK)

    @Column(nullable = false)
    val name: String, // Email

    @ElementCollection
    val participants: MutableList<String>
)