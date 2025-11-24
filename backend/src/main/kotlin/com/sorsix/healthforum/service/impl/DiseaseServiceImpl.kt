package com.sorsix.healthforum.service.impl

import com.sorsix.healthforum.model.Disease
import com.sorsix.healthforum.model.enumerations.DiseaseCategory
import com.sorsix.healthforum.model.exceptions.DiseaseAlreadyExistsException
import com.sorsix.healthforum.model.exceptions.DiseaseNotFoundException
import com.sorsix.healthforum.repository.DiseaseRepository
import com.sorsix.healthforum.service.DiseaseService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class DiseaseServiceImpl(
    private val diseaseRepository: DiseaseRepository
) : DiseaseService {
    override fun getAllDiseases(): List<Disease> {
        return diseaseRepository.findAll()
    }

    override fun getDiseaseById(id: Long): Disease {
        return diseaseRepository.findById(id).orElseThrow {
            DiseaseNotFoundException.byId(id.toString())
        }
    }

    override fun getDiseaseByCategory(diseaseCategory: DiseaseCategory): List<Disease> {
        return diseaseRepository.findByCategory(diseaseCategory)
    }

    @Transactional
    override fun saveDisease(disease: Disease): Disease {
        val name = disease.name?.trim() ?: throw IllegalArgumentException("Disease name must be provided")
        val description = disease.description?.trim() ?: throw IllegalArgumentException("Disease description must be provided")
        require(name.isNotBlank()) { "Name must not be blank" }
        require(description.isNotBlank()) { "Description must not be blank" }
        require(disease.category != null) { "Disease category must be specified" }

        if (diseaseRepository.existsByNameIgnoreCase(name)) {
            throw DiseaseAlreadyExistsException.byName(name)
        }

        return diseaseRepository.save(disease.copy(name = name))
    }

    @Transactional
    override fun deleteDisease(id: Long) {
        if (!diseaseRepository.existsById(id)) {
            throw DiseaseNotFoundException.byId(id.toString())
        }
        diseaseRepository.deleteById(id)
    }
}