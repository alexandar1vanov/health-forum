package com.sorsix.healthforum.model.dto.profile_response_dtos

import com.sorsix.healthforum.model.User
import java.util.Date

// model/dto/ProfileResponse.kt
data class ProfileResponse(
    val id: Long,
    val email: String,
    val name: String?,
    val surname: String?,
    val diseases: List<String>,
    val hasSelectedDiseases: Boolean,
    val createdAt: Date?
)

// Mapping функција на User. Болестите се читаат преку UserDisease,
// затоа се проследуваат како параметар.
fun User.toProfileResponse(diseases: List<String>) = ProfileResponse(
    id = id!!,
    email = email,
    name = name,
    surname = surname,
    diseases = diseases,
    hasSelectedDiseases = hasSelectedDiseases,
    createdAt = createdAt
)