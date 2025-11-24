package com.sorsix.healthforum.service

import com.sorsix.healthforum.model.UserDisease
import com.sorsix.healthforum.model.dto.user_disease_dtos.UserDiseaseDetailsDTO
import com.sorsix.healthforum.model.dto.disease_dtos.DiseasesDTO
import com.sorsix.healthforum.model.dto.users_dtos.UsersDTO

interface UserDiseaseService {

    fun getUserDiseases(userId: Long): List<DiseasesDTO>

    fun getDiseaseUsers(diseaseId: Long): List<UsersDTO>

    fun addDiseasesToUser(userId: Long, diseaseIds: List<Long>): List<UserDisease>

    fun removeDiseaseFromUser(userId: Long, diseaseId: Long)

    fun getUserDisease(id: Long): UserDiseaseDetailsDTO

}