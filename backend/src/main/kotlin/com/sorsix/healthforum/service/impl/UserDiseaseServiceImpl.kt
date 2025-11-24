package com.sorsix.healthforum.service.impl

import com.sorsix.healthforum.model.UserDisease
import com.sorsix.healthforum.model.dto.user_disease_dtos.UserDiseaseDetailsDTO
import com.sorsix.healthforum.model.dto.disease_dtos.DiseasesDTO
import com.sorsix.healthforum.model.dto.users_dtos.UsersDTO
import com.sorsix.healthforum.model.exceptions.UserDiseaseAssociationNotFoundException
import com.sorsix.healthforum.model.exceptions.UserDiseaseNotFoundException
import com.sorsix.healthforum.repository.UserDiseaseRepository
import com.sorsix.healthforum.service.DiseaseService
import com.sorsix.healthforum.service.UserDiseaseService
import com.sorsix.healthforum.service.UserService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserDiseaseServiceImpl(
    private val userDiseaseRepository: UserDiseaseRepository,
    private val userService: UserService,
    private val diseaseService: DiseaseService
) : UserDiseaseService {

    override fun getUserDiseases(userId: Long): List<DiseasesDTO> {
        return userDiseaseRepository.findByUserId(userId)
            .map { userDisease ->
                val disease = diseaseService.getDiseaseById(userDisease.disease?.id!!)
                DiseasesDTO(
                    id = disease.id!!,
                    name = disease.name!!,
                    category = disease.category!!,
                    description = disease.description!!
                )
            }
    }

    override fun getDiseaseUsers(diseaseId: Long): List<UsersDTO> {
        return userDiseaseRepository.findByDiseaseId(diseaseId)
            .map { userDisease ->
                UsersDTO(
                    email = userDisease.user?.email!!,
                )
            }
    }

    @Transactional
    override fun addDiseasesToUser(userId: Long, diseaseIds: List<Long>): List<UserDisease> {
        val user = userService.getUserById(userId)
        val result = mutableListOf<UserDisease>()

        for (diseaseId in diseaseIds) {
            if (userDiseaseRepository.existsByUserIdAndDiseaseId(userId, diseaseId)) {
                continue
            }
            val disease = diseaseService.getDiseaseById(diseaseId)
            val userDisease = UserDisease(
                user = user,
                disease = disease
            )
            result.add(userDiseaseRepository.save(userDisease))
        }

        if (result.isNotEmpty()) {
            user.hasSelectedDiseases = true
            userService.saveUser(user)
        }

        return result
    }

    @Transactional
    override fun removeDiseaseFromUser(userId: Long, diseaseId: Long) {
        val userDisease = userDiseaseRepository.findByUserIdAndDiseaseId(userId, diseaseId)
            ?: throw UserDiseaseAssociationNotFoundException(userId, diseaseId)

        userDiseaseRepository.delete(userDisease)
    }

    override fun getUserDisease(id: Long): UserDiseaseDetailsDTO {
        val userDisease =  userDiseaseRepository.findById(id)
            .orElseThrow {
                UserDiseaseNotFoundException(id)
            }
        val userDiseases = userDiseaseRepository.findByUserId(userDisease.user?.id!!)
        val diseases = userDiseases.map { ud ->
            DiseasesDTO(
                id = ud.disease?.id!!,
                name = ud.disease.name!!,
                category = ud.disease.category!!,
                description = ud.disease.description!!
            )
        }
        return UserDiseaseDetailsDTO(
            user = userDisease.user.email,
            diseases = diseases,
            hasSelectedDiseases = userDisease.user.hasSelectedDiseases
        )
    }
}