package com.sorsix.healthforum.service

import com.sorsix.healthforum.repository.UserRepository
import com.sorsix.healthforum.security.UserSecurity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {

        val user = userRepository.findByEmail(username)
            .orElseThrow { Exception("User not Found with email: $username") }

        user.id?.let {
            return UserSecurity(
                it,
                user.email,
                user.password,
                user.role
            )
        } ?: run {
            throw Exception("User not found")
        }
    }
}