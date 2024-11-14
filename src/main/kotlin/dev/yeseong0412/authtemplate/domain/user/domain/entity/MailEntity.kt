package dev.yeseong0412.authtemplate.domain.user.domain.entity

import jakarta.persistence.*

@Entity
class MailEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null, // ID (PK)
    @Column
    val email : String,
    @Column
    val authCode : String
)