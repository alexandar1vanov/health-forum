package com.sorsix.healthforum.model.exceptions

class ReplyNotFoundException(message: String) : RuntimeException(message) {
    companion object {
        fun byId(id: String): ReplyNotFoundException =
            ReplyNotFoundException("Reply with ID: $id not found")
    }
}