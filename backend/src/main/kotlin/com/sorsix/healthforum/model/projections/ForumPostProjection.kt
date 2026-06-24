package com.sorsix.healthforum.model.projections

import java.time.LocalDateTime

interface ForumPostProjection {
    val id: Long
    val title: String
    val content: String
    val userId: Long
    val userEmail: String
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime
    val rating: Double?
    val likeCount: Long
}