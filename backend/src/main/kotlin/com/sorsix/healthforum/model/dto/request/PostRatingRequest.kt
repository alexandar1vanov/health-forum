package com.sorsix.healthforum.model.dto.request

data class PostRatingRequest(
    val postId: Long,
    val rating: Int,
)