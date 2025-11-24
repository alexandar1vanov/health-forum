package com.sorsix.healthforum.model.dto.admin_dtos

import com.sorsix.healthforum.model.enumerations.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class AdminUserUpdateDTO(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Must be a valid email address")
    val email: String? = null,

    @NotNull(message = "Role is required")
    val role: Role? = null,
)