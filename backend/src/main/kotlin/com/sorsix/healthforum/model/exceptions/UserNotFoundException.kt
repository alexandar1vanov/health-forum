package com.sorsix.healthforum.model.exceptions

class UserNotFoundException(message: String) : RuntimeException(message) {
    companion object {
        fun byId(id: String): UserNotFoundException =
            UserNotFoundException("User with ID $id Not Found")

        fun byEmail(email: String): UserNotFoundException =
            UserNotFoundException("User with email $email Not Found")
    }
}