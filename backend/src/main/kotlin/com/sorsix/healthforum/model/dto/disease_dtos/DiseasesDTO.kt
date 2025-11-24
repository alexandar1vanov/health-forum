package com.sorsix.healthforum.model.dto.disease_dtos

import com.sorsix.healthforum.model.enumerations.DiseaseCategory

data class DiseasesDTO(
    val id: Long,
    val name: String,
    val category: DiseaseCategory,
    val description: String,
)
