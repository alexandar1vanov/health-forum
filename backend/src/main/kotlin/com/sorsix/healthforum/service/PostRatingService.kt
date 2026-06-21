package com.sorsix.healthforum.service

import com.sorsix.healthforum.model.dto.request.PostRatingRequest
import com.sorsix.healthforum.model.dto.response.PostRatingDTO

interface PostRatingService {
    fun submitRating(postRatingRequest: PostRatingRequest): PostRatingDTO

    fun getRatingsByActiveUser(): List<PostRatingDTO>

    fun getRatingByPostId(postId: Long): PostRatingDTO

    fun deleteRating(postId: Long)
}