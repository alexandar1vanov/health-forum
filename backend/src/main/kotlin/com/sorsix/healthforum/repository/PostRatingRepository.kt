package com.sorsix.healthforum.repository

import com.sorsix.healthforum.model.PostRating
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PostRatingRepository : JpaRepository<PostRating, Long> {
    fun findPostRatingsByUserId(userId: Long): List<PostRating>
    fun findByPostIdAndUserId(postId: Long, userId: Long): PostRating?
}