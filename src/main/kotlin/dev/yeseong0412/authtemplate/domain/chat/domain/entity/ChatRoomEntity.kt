package dev.yeseong0412.authtemplate.domain.chat.domain.entity

import dev.yeseong0412.authtemplate.domain.user.domain.entity.UserEntity
import dev.yeseong0412.authtemplate.global.common.BaseEntity
import jakarta.persistence.*

@Entity(name = "chatroom")
class ChatRoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null, // ID (PK)

    @Column(nullable = false)
    var name: String, // Email

    @ManyToMany(mappedBy = "rooms", fetch = FetchType.LAZY)
    val participants: MutableSet<UserEntity> = mutableSetOf(),

    @Column(nullable = false)
    val adminId: Long
) : BaseEntity()