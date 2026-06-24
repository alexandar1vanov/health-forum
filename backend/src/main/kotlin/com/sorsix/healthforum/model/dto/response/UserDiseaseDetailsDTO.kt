package com.sorsix.healthforum.model.dto.response

data class UserDiseaseDetailsDTO(
    val user: String,
    val diseases: List<DiseasesDTO>,
    val hasSelectedDiseases: Boolean
)