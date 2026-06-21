package com.sorsix.healthforum.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityUtil {

    fun getActiveUser(): UserSecurity {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No authentication found")

        val principal = authentication.principal

        return principal as? UserSecurity
            ?: throw IllegalStateException("Principal is not UserSecurity: ${principal::class}")
    }

    fun getActiveUserId(): Long {
        return getActiveUser().id
            ?: throw IllegalStateException("User ID is null")
    }
}