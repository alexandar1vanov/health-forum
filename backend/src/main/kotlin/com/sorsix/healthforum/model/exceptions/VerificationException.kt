package com.sorsix.healthforum.model.exceptions

class VerificationException(message: String) : RuntimeException(message) {
    companion object {
        fun invalidToken(): VerificationException =
            VerificationException("Invalid verification link.")

        fun alreadyUsed(): VerificationException =
            VerificationException("This verification link has already been used.")

        fun expired(): VerificationException =
            VerificationException("This verification link has expired. Please request a new one.")
    }
}