package com.sorsix.healthforum.model.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class  CreateReplyRequest(
    @field:NotBlank(message = "Reply is required")
    @field:Size(max = 500, message = "Reply must be under 500 characters")
    val content: String,

    val userId: Long,

    val commentId: Long
)
