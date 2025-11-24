package com.sorsix.healthforum.model.dto.auth_dtos

import java.beans.ConstructorProperties

data class LoginDTO
@ConstructorProperties("email", "password")
    constructor(
    val email: String,
    val password: String
)