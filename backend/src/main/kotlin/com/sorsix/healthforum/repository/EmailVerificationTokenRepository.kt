package com.sorsix.healthforum.repository

import com.sorsix.healthforum.model.EmailVerificationToken
import com.sorsix.healthforum.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface EmailVerificationTokenRepository : JpaRepository<EmailVerificationToken, Long> {
    fun findByToken(token: String): Optional<EmailVerificationToken>

    fun findByUserAndUsedFalse(user: User): List<EmailVerificationToken>
}