package com.sorsix.healthforum.service.impl

import com.sorsix.healthforum.model.PostRating
import com.sorsix.healthforum.model.dto.request.PostRatingRequest
import com.sorsix.healthforum.model.dto.response.PostRatingDTO
import com.sorsix.healthforum.model.extensions.toPostRatingDTO
import com.sorsix.healthforum.repository.ForumPostRepository
import com.sorsix.healthforum.repository.PostRatingRepository
import com.sorsix.healthforum.repository.UserRepository
import com.sorsix.healthforum.security.SecurityUtil
import com.sorsix.healthforum.service.PostRatingService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostRatingServiceImpl(
    private val postRatingRepository: PostRatingRepository,
    private val userRepository: UserRepository,
    private val forumPostRepository: ForumPostRepository,
    private val securityUtil: SecurityUtil,
) : PostRatingService {

    @Transactional
    override fun submitRating(postRatingRequest: PostRatingRequest): PostRatingDTO {
        val userId = securityUtil.getActiveUserId()

        val postRating = postRatingRepository.findByPostIdAndUserId(postRatingRequest.postId, userId)

        if (postRating != null) {
            postRating.rating = postRatingRequest.rating
            val updatedPostRating = postRatingRepository.save(postRating)
            return updatedPostRating.toPostRatingDTO()
        }

        val post = forumPostRepository.findById(postRatingRequest.postId)
            .orElseThrow { RuntimeException("Post not found") }

        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }

        val ratingToSubmit = PostRating(
            post = post,
            user = user,
            rating = postRatingRequest.rating
        )

        val savedRating = postRatingRepository.save(ratingToSubmit)
        return savedRating.toPostRatingDTO()
    }

    override fun getRatingsByActiveUser(): List<PostRatingDTO> {
        val activeUserId = securityUtil.getActiveUserId()

        return postRatingRepository.findPostRatingsByUserId(activeUserId).map { it.toPostRatingDTO() }
    }

    override fun getRatingByPostId(postId: Long): PostRatingDTO {
        val activeUserId = securityUtil.getActiveUserId()

        return postRatingRepository.findByPostIdAndUserId(postId, activeUserId)?.toPostRatingDTO()
            ?: throw RuntimeException("Rating with userId $activeUserId or postId $postId was not found")
    }

    @Transactional
    override fun deleteRating(postId: Long) {
        val activeUserId = securityUtil.getActiveUserId()

        val postRating = postRatingRepository.findByPostIdAndUserId(postId, activeUserId)
            ?: throw RuntimeException("Rating with userId $activeUserId or postId $postId was not found")

        postRatingRepository.delete(postRating)
    }
}