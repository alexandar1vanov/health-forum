package com.sorsix.healthforum.service

import com.sorsix.healthforum.model.Disease
import com.sorsix.healthforum.model.enumerations.DiseaseCategory

interface DiseaseService {

    fun getAllDiseases(): List<Disease>

    fun getDiseaseById(id: Long): Disease

    fun getDiseaseByCategory(diseaseCategory: DiseaseCategory): List<Disease>

    fun saveDisease(disease: Disease): Disease

    fun deleteDisease(id: Long)

}