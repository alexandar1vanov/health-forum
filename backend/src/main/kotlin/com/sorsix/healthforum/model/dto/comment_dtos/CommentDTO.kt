package com.sorsix.healthforum.model.dto.comment_dtos

import com.sorsix.healthforum.model.dto.forum_post_dtos.ForumPostDTO
import com.sorsix.healthforum.model.dto.users_dtos.UsersDTO
import java.time.LocalDateTime

data class CommentDTO(
    val id: Long,
    val user: UsersDTO,
    val forumPost: ForumPostDTO,
    val content: String,
    val createdAt: LocalDateTime,
)