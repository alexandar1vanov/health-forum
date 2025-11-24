package com.sorsix.healthforum.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime


@Entity
@Table(name = "forum_posts")
data class ForumPost(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @NotBlank(message = "Title is required")
    @Size(max = 30, message = "Title must be under 30 characters")
    @Column(name = "post_title", nullable = false, length = 30)
    val title: String? = null,

    @NotBlank(message = "Content is required")
    @Size(max = 1000, message = "Content must be under 1000 characters")
    @Column(name = "post_content", nullable = false, length = 1000)
    val content: String? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User? = null ,

    @ManyToMany
    @JoinTable(
        name = "post_diseases",
        joinColumns = [JoinColumn(name = "post_id")],
        inverseJoinColumns = [JoinColumn(name = "disease_id")]
    )
    val diseases: MutableSet<Disease> = mutableSetOf(),

    @CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null
)