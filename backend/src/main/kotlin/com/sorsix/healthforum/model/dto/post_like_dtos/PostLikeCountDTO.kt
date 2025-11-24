package com.sorsix.healthforum.model.dto.post_like_dtos

data class PostLikeCountDTO(
    val postId: Long,
    val likeCount: Long,
    val isLikedByCurrentUser: Boolean
)