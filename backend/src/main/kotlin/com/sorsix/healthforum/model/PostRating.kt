package com.sorsix.healthforum.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "post_ratings")
data class PostRating(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne()
    @JoinColumn(name = "post_id")
    var post: ForumPost? = null,

    @ManyToOne()
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @Column(name = "rating")
    var rating: Int? = null,
)