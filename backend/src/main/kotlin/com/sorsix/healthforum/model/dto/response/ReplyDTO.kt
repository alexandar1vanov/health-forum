package com.sorsix.healthforum.model.dto.response

import java.time.LocalDateTime

data class ReplyDTO(
    val id: Long,
    val content: String,
    val user: UsersDTO,
    val comment: CommentRepliedDTO,
    val createdAt: LocalDateTime,
)
