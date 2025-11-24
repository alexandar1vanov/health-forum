package com.sorsix.healthforum.controller

import com.sorsix.healthforum.model.UserDisease
import com.sorsix.healthforum.model.dto.user_disease_dtos.UserDiseaseDetailsDTO
import com.sorsix.healthforum.model.dto.disease_dtos.DiseasesDTO
import com.sorsix.healthforum.model.dto.users_dtos.UsersDTO
import com.sorsix.healthforum.service.UserDiseaseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user-diseases")
class UserDiseaseController(
    private val userDiseaseService: UserDiseaseService
) {


    @GetMapping("/user/{userId}")
    fun getUserDiseases(@PathVariable userId: Long): ResponseEntity<List<DiseasesDTO>> {
        return ResponseEntity.ok(userDiseaseService.getUserDiseases(userId))
    }

    @GetMapping("/disease/{diseaseId}")
        fun getDiseaseUsers(@PathVariable diseaseId: Long): ResponseEntity<List<UsersDTO>> {
        return ResponseEntity.ok(userDiseaseService.getDiseaseUsers(diseaseId))
    }

    @PostMapping("/{userId}")
    fun addDiseasesToUser(
        @PathVariable userId: Long,
        @RequestBody diseaseIds: List<Long>
    ): ResponseEntity<List<UserDisease>> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userDiseaseService.addDiseasesToUser(userId, diseaseIds))
    }

    @DeleteMapping("/{userId}/disease/{diseaseId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun removeDiseaseFromUser(
        @PathVariable userId: Long,
        @PathVariable diseaseId: Long
    ): ResponseEntity<Void> {
        userDiseaseService.removeDiseaseFromUser(userId, diseaseId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}")
    fun getUserDisease(@PathVariable id: Long): ResponseEntity<UserDiseaseDetailsDTO> {
        return ResponseEntity.ok(userDiseaseService.getUserDisease(id))
    }

}