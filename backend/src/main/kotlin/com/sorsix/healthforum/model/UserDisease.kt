package com.sorsix.healthforum.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity
@Table(name = "user_diseases")
data class UserDisease(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User? = null,

    @ManyToOne
    @JoinColumn(name = "disease_id", nullable = false)
    val disease: Disease? = null,

    @CreationTimestamp
    val createdAt: Date? = null,

    @UpdateTimestamp
    var updatedAt: Date? = null,
    )