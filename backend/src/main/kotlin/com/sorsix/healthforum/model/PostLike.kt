package com.sorsix.healthforum.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "post_likes",
    uniqueConstraints = [UniqueConstraint(columnNames = ["post_id", "user_id"])])
data class PostLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    val post: ForumPost,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @CreationTimestamp
    val createdAt: LocalDateTime? = null
){
    constructor() : this(
        id = null,
        post = ForumPost(),
        user = User(),
        createdAt = null
    )
}