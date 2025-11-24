package com.sorsix.healthforum.model

import jakarta.persistence.Entity
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime


@Entity
@Table(name = "replies")
data class Reply(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @NotBlank(message = "Reply is required")
    @Size(max = 500, message = "Reply must be less than 500 characters")
    @Column(name = "reply_content", nullable = false, length = 500)
    val content: String? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User? = null,

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    val comment: Comment? = null,

    @CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null

)