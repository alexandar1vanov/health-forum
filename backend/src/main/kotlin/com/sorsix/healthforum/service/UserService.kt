package com.sorsix.healthforum.service

import com.sorsix.healthforum.model.User
import com.sorsix.healthforum.model.dto.admin_dtos.AdminUserUpdateDTO
import com.sorsix.healthforum.model.dto.auth_dtos.SignUpDTO
import com.sorsix.healthforum.model.dto.users_dtos.UserPanelDTO
import com.sorsix.healthforum.model.exceptions.UserNotFoundException
import com.sorsix.healthforum.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import toUserPanelDTO
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val userRepository: UserRepository,
    val passwordEncoder: BCryptPasswordEncoder
) {
    fun getUserByEmail(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }

    fun getUserById(id: Long): User {
        return userRepository.findById(id).orElseThrow{ UserNotFoundException.byId(id.toString()) }
    }

    fun saveUser(user: User): User {
        return userRepository.save(user)
    }

    fun getAllUsers(): List<UserPanelDTO> {
        return userRepository.getActiveUsers().map {
            it.toUserPanelDTO()
        }
    }

    @Transactional
    fun signUpUser(signUpDTO: SignUpDTO) {

        if (signUpDTO.email?.let { getUserByEmail(it).getOrNull() } == null) {
            val user = User()
            user.email = signUpDTO.email ?: throw Exception("Unable to create user, provide an email")
            user.password = passwordEncoder.encode(signUpDTO.password)
            saveUser(user)
        } else {
            throw Exception("Unable to sign up")
        }
    }

    @Transactional
    fun updateUser(id: Long, updateDTO: AdminUserUpdateDTO): User {
        val user = userRepository.findById(id).orElseThrow {
            UserNotFoundException.byId(id.toString())
        }

        updateDTO.email?.let { user.email = it }
        updateDTO.role?.let { user.role = it }

        return userRepository.save(user)
    }

    fun searchUserByEmail(email: String): UserPanelDTO {
        return userRepository.searchUserByEmail(email).toUserPanelDTO()
    }

    fun deleteUser(id: Long) {
        userRepository.findById(id).ifPresent { user ->
            user.isDeleted = true
            userRepository.save(user)
        }
    }

}