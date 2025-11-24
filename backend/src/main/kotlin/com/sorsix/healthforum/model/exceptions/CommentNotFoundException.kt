package com.sorsix.healthforum.model.exceptions

class CommentNotFoundException(message: String) : RuntimeException(message) {
    companion object {
        fun byId(id: String): CommentNotFoundException =
            CommentNotFoundException("Comment with ID $id not found")
    }
}