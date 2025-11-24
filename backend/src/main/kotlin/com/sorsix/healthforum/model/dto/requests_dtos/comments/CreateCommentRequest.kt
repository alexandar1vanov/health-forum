package com.sorsix.healthforum.model.dto.requests_dtos.comments

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateCommentRequest(
    val userId: Long,
    val forumPostId: Long,
    @field:NotBlank(message = "Content is required")
    @field: Size(max = 1000, message = "Content must be under 1000 characters")
    val content: String
)
