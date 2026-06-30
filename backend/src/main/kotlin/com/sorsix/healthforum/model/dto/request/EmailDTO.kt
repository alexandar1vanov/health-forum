package com.sorsix.healthforum.model.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailDTO(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Must be a valid email address")
    var email: String?
)