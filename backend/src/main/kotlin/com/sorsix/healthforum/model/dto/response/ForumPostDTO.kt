package com.sorsix.healthforum.model.dto.response

import java.time.LocalDateTime

data class ForumPostDTO(
    val id: Long,
    val title: String,
    val content: String,
    val user: UsersDTO,
    val likeCount: Long=0,
    val rating: Double=0.0,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isLikedByCurrentUser: Boolean = false
)