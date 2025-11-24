package com.sorsix.healthforum.model.dto.reply_dtos

import com.sorsix.healthforum.model.dto.users_dtos.UsersDTO
import java.time.LocalDateTime

data class ReplyDTO(
    val id: Long,
    val content: String,
    val user: UsersDTO,
    val comment: CommentRepliedDTO,
    val createdAt: LocalDateTime,
)
