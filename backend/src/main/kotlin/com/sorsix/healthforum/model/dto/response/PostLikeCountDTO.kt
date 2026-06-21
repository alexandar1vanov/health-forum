package com.sorsix.healthforum.model.dto.response

data class PostLikeCountDTO(
    val postId: Long,
    val likeCount: Long,
    val isLikedByCurrentUser: Boolean
)