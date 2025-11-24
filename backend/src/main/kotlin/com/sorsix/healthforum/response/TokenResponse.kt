package com.sorsix.healthforum.response

data class TokenResponse(
    val token: String,
    val selectedDiseases: Boolean,
    val role: String
)
