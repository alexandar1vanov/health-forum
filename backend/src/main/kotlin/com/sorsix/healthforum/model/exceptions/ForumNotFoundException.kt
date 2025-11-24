package com.sorsix.healthforum.model.exceptions

class ForumNotFoundException(message: String) : RuntimeException(message){
    companion object {
        fun byId(id: String): ForumNotFoundException =
            ForumNotFoundException("Forum ID $id not found")
    }
}
