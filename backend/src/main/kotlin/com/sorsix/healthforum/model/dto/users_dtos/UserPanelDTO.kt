package com.sorsix.healthforum.model.dto.users_dtos

import com.sorsix.healthforum.model.enumerations.Role
import java.util.*

data class UserPanelDTO(
    val id: Long?,
    val email: String,
    val role: Role,
    val hasSelectedDiseases: Boolean,
    val createdAt: Date?,
)
