package com.sorsix.healthforum.service

import com.sorsix.healthforum.model.User
import com.sorsix.healthforum.model.dto.request.AdminUserUpdateDTO
import com.sorsix.healthforum.model.dto.response.SignUpDTO
import com.sorsix.healthforum.model.dto.response.UserPanelDTO
import com.sorsix.healthforum.model.dto.profile_response_dtos.ProfileResponse
import com.sorsix.healthforum.model.dto.profile_response_dtos.UpdateProfileRequest
import com.sorsix.healthforum.model.dto.profile_response_dtos.toProfileResponse
import com.sorsix.healthforum.model.EmailVerificationToken
import com.sorsix.healthforum.model.exceptions.UserNotFoundException
import com.sorsix.healthforum.model.exceptions.VerificationException
import com.sorsix.healthforum.model.extensions.toUserPanelDTO
import com.sorsix.healthforum.repository.EmailVerificationTokenRepository
import com.sorsix.healthforum.repository.UserDiseaseRepository
import com.sorsix.healthforum.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Optional
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userDiseaseRepository: UserDiseaseRepository,
    private val emailService: EmailService,
    private val verificationTokenRepository: EmailVerificationTokenRepository,
    val passwordEncoder: BCryptPasswordEncoder,
    @Value("\${health_forum.frontend.url}") private val frontendUrl: String
) {
    fun getProfile(id: Long): ProfileResponse {
        val user = getUserById(id)
        val diseases = userDiseaseRepository.findByUserId(id)
            .mapNotNull { it.disease?.name }
        return user.toProfileResponse(diseases)
    }

    @Transactional
    fun updateProfile(id: Long, request: UpdateProfileRequest): ProfileResponse {
        val user = getUserById(id)
        request.name?.let { user.name = it }
        request.surname?.let { user.surname = it }
        userRepository.save(user)
        return getProfile(id)
    }

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
            user.isVerified = false
            val savedUser = saveUser(user)
            sendVerificationToken(savedUser)
        } else {
            throw Exception("Unable to sign up")
        }
    }

    private fun sendVerificationToken(user: User) {
        val verificationToken = EmailVerificationToken(
            token = UUID.randomUUID().toString(),
            user = user,
            expiresAt = Instant.now().plus(24, ChronoUnit.HOURS),
            used = false
        )
        verificationTokenRepository.save(verificationToken)
        try {
            val link = "$frontendUrl/verify-email?token=${verificationToken.token}"
            emailService.sendVerificationEmail(user.email, link)
        } catch (e: Exception) {
            // праќањето мејл не смее да ја урне регистрацијата
            println("Failed to send verification email: ${e.message}")
        }
    }

    @Transactional
    fun verifyEmail(token: String) {
        val verificationToken = verificationTokenRepository.findByToken(token).getOrNull()
            ?: throw VerificationException.invalidToken()
        if (verificationToken.used) throw VerificationException.alreadyUsed()
        if (verificationToken.expiresAt.isBefore(Instant.now())) throw VerificationException.expired()

        val user = verificationToken.user ?: throw VerificationException.invalidToken()
        user.isVerified = true
        userRepository.save(user)
        verificationToken.used = true
        verificationTokenRepository.save(verificationToken)

        try {
            emailService.sendRegistrationSuccess(user.email)
        } catch (e: Exception) {
            println("Failed to send registration success email: ${e.message}")
        }
    }

    @Transactional
    fun resendVerification(email: String) {
        val user = getUserByEmail(email).getOrNull() ?: return
        if (user.isVerified) return
        verificationTokenRepository.findByUserAndUsedFalse(user).forEach {
            it.used = true
            verificationTokenRepository.save(it)
        }
        sendVerificationToken(user)
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