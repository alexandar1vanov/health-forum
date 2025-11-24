package com.sorsix.healthforum.repository

import com.sorsix.healthforum.model.UserDisease
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDiseaseRepository : JpaRepository<UserDisease, Long> {

    fun findByUserId(userId: Long): List<UserDisease>
    fun findByDiseaseId(diseaseId: Long): List<UserDisease>
    fun findByUserIdAndDiseaseId(userId: Long, diseaseId: Long): UserDisease?
    fun existsByUserIdAndDiseaseId(userId: Long, diseaseId: Long): Boolean
}