package com.sorsix.healthforum.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "email_verification_token")
data class EmailVerificationToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var token: String = "",

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant = Instant.now(),

    @Column(nullable = false)
    var used: Boolean = false
)