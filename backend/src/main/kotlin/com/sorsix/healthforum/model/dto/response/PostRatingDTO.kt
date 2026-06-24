package com.sorsix.healthforum.model.dto.response

data class PostRatingDTO (
    val id: Long,
    val userId: Long?,
    val postId: Long?,
    val rating: Int?,
)