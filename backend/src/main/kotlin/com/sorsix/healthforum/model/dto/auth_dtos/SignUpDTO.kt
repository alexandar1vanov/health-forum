package com.sorsix.healthforum.model.dto.auth_dtos

import jakarta.validation.constraints.*

data class SignUpDTO
constructor(

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Must be a valid email address")
    var email: String?,

    @field:NotBlank(message = "Password is required")
    var password: String?,

)