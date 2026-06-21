package com.sorsix.healthforum.model.dto.response

import java.time.LocalDateTime

data class CommentDTO(
    val id: Long,
    val user: UsersDTO,
    val forumPost: ForumPostDTO,
    val content: String,
    val createdAt: LocalDateTime,
)