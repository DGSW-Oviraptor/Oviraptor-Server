package dev.yeseong0412.authtemplate.domain.user.domain.entity

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
    val friends: MutableList<UserEntity> = mutableListOf(),

    @Column(nullable = false)
    var role: UserRoles = UserRoles.ROLE_USER

)