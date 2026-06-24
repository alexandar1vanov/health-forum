package com.sorsix.healthforum.model.dto.response

import java.beans.ConstructorProperties

data class LoginDTO
@ConstructorProperties("email", "password")
    constructor(
    val email: String,
    val password: String
)