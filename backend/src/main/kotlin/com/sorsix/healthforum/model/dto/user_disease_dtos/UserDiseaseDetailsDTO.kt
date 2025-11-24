package com.sorsix.healthforum.model.dto.user_disease_dtos

import com.sorsix.healthforum.model.dto.disease_dtos.DiseasesDTO

data class UserDiseaseDetailsDTO(
    val user: String,
    val diseases: List<DiseasesDTO>,
    val hasSelectedDiseases: Boolean
)
