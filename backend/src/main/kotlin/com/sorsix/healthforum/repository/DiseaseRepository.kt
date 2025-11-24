package com.sorsix.healthforum.repository

import com.sorsix.healthforum.model.Disease
import com.sorsix.healthforum.model.enumerations.DiseaseCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DiseaseRepository : JpaRepository<Disease, Long> {

    fun findByCategory(category: DiseaseCategory): List<Disease>

    fun existsByNameIgnoreCase(name: String): Boolean
}