package com.sorsix.healthforum.model.dto.post_like_dtos

data class PostLikeDTO(
    val id: Long?,
    val postId: Long,
    val userId: Long,
    val username: String,
    val createdAt: String
)