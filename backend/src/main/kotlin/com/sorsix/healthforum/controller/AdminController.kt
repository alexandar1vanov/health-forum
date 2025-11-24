package com.sorsix.healthforum.controller

import com.sorsix.healthforum.model.User
import com.sorsix.healthforum.model.dto.admin_dtos.AdminUserUpdateDTO
import com.sorsix.healthforum.model.dto.users_dtos.UserPanelDTO
import com.sorsix.healthforum.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val userService: UserService
) {

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getAllUsers(): ResponseEntity<List<UserPanelDTO>>{
        return ResponseEntity(userService.getAllUsers(), HttpStatus.OK)
    }

    @GetMapping("/searched")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getSearchedUser(
        @RequestParam email: String,
    ): ResponseEntity<UserPanelDTO>{
        return ResponseEntity(userService.searchUserByEmail(email), HttpStatus.OK)
    }

    @PutMapping("/user/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody userUpdateDTO: AdminUserUpdateDTO
    ): ResponseEntity<User> {
        return ResponseEntity.ok(userService.updateUser(id, userUpdateDTO))
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

}