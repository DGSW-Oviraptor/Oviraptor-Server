package dev.yeseong0412.authtemplate.domain.user.domain.entity

import dev.yeseong0412.authtemplate.domain.chat.domain.entity.ChatRoomEntity
import dev.yeseong0412.authtemplate.domain.user.domain.enums.UserRoles
import jakarta.persistence.*

@Entity
class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null, // ID (PK)

    @Column(nullable = false)
    var email: String, // Email

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var password: String, // Password

    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "friend_id")]
    )
    val friends: MutableSet<UserEntity> = mutableSetOf(),

    @ManyToMany
    @JoinTable(
        name = "user_chatroom",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "chatroom_id")]
    )
    val rooms: MutableList<ChatRoomEntity> = mutableListOf(),

    @Column(nullable = false)
    var role: UserRoles = UserRoles.ROLE_USER

)