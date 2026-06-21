package com.sorsix.healthforum.model.dto.response

data class PostLikeDTO(
    val id: Long?,
    val postId: Long,
    val userId: Long,
    val username: String,
    val createdAt: String
)