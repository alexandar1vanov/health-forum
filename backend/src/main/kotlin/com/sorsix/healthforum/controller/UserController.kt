package com.sorsix.healthforum.controller

import com.sorsix.healthforum.model.dto.response.SignUpDTO
import com.sorsix.healthforum.model.dto.profile_response_dtos.ProfileResponse
import com.sorsix.healthforum.model.dto.profile_response_dtos.UpdateProfileRequest
import com.sorsix.healthforum.model.exceptions.UserNotFoundException
import com.sorsix.healthforum.service.UserDiseaseService
import com.sorsix.healthforum.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrNull

@RestController
class UserController(
    val userService: UserService,
    val userDiseaseService: UserDiseaseService
) {

    @PostMapping("/api/signup")
    fun signUp(@RequestBody @Valid signUpDTO: SignUpDTO): ResponseEntity<String> {
        try {
            userService.signUpUser(signUpDTO)
        } catch (e: Exception) {
            return ResponseEntity(e.message, HttpStatus.CONFLICT)
        }
        return ResponseEntity("Created", HttpStatus.CREATED)
    }

    @GetMapping("/api/profile")
    fun getProfile(authentication: Authentication): ResponseEntity<ProfileResponse> {
        return ResponseEntity.ok(userService.getProfile(currentUserId(authentication)))
    }

    @PutMapping("/api/profile")
    fun updateProfile(
        authentication: Authentication,
        @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<ProfileResponse> {
        return ResponseEntity.ok(userService.updateProfile(currentUserId(authentication), request))
    }

    @PostMapping("/api/profile/diseases")
    fun addDiseases(
        authentication: Authentication,
        @RequestBody diseaseIds: List<Long>
    ): ResponseEntity<ProfileResponse> {
        val userId = currentUserId(authentication)
        userDiseaseService.addDiseasesToUser(userId, diseaseIds)
        return ResponseEntity.ok(userService.getProfile(userId))
    }

    @DeleteMapping("/api/profile/diseases/{diseaseId}")
    fun removeDisease(
        authentication: Authentication,
        @PathVariable diseaseId: Long
    ): ResponseEntity<ProfileResponse> {
        val userId = currentUserId(authentication)
        userDiseaseService.removeDiseaseFromUser(userId, diseaseId)
        return ResponseEntity.ok(userService.getProfile(userId))
    }

    private fun currentUserId(authentication: Authentication): Long {
        val user = userService.getUserByEmail(authentication.name).getOrNull()
            ?: throw UserNotFoundException.byEmail(authentication.name)
        return user.id!!
    }

}