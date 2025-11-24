package com.sorsix.healthforum.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "forum_comments")
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @ManyToOne
    @JoinColumn(name = "forum_post")
    val forumPost: ForumPost? = null,

    @Column(name = "comment_content", nullable = false, length = 1000)
    @field:NotBlank(message = "Content is required")
    @field:Size(max = 1000, message = "Content must be under 1000 characters")
    val content: String? = null,

    @CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null

)
