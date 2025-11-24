package com.sorsix.healthforum.controller

import com.sorsix.healthforum.model.dto.auth_dtos.SignUpDTO
import com.sorsix.healthforum.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    val userService: UserService
) {

    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid signUpDTO: SignUpDTO): ResponseEntity<String> {
        try {
            userService.signUpUser(signUpDTO)
        } catch (e: Exception) {
            return ResponseEntity(e.message, HttpStatus.CONFLICT)
        }
        return ResponseEntity("Created", HttpStatus.CREATED)
    }

}