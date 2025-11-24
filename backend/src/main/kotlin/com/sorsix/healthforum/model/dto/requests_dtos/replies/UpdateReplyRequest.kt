package com.sorsix.healthforum.model.dto.requests_dtos.replies

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateReplyRequest(
    @field:NotBlank(message = "Content is required")
    @field:Size(max = 500, message = "Reply must be under 500 characters")
    val content: String,
)
