package com.sorsix.healthforum.service.impl

import com.sorsix.healthforum.model.PostLike
import com.sorsix.healthforum.model.dto.post_like_dtos.PostLikeCountDTO
import com.sorsix.healthforum.model.dto.post_like_dtos.PostLikeDTO
import com.sorsix.healthforum.repository.ForumPostRepository
import com.sorsix.healthforum.repository.PostLikeRepository
import com.sorsix.healthforum.repository.UserRepository
import com.sorsix.healthforum.service.PostLikeService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class PostLikeServiceImpl(
    private val postLikeRepository: PostLikeRepository,
    private val forumPostRepository: ForumPostRepository,
    private val userRepository: UserRepository
) : PostLikeService {
    override fun getLikesForPost(postId: Long): List<PostLikeDTO> {
        return postLikeRepository.findAll().filter { it.post.id == postId }.map { mapToDto(it) }
    }

    override fun getLikeCount(postId: Long, currentUserId: Long?): PostLikeCountDTO {
        val isLiked = currentUserId?.let {
            postLikeRepository.existsByPostIdAndUserId(postId, it)
        } ?: false

        val likeCount = postLikeRepository.countByPostId(postId)

        return PostLikeCountDTO(
            postId = postId,
            likeCount = likeCount,
            isLikedByCurrentUser = isLiked
        )
    }

    fun getLikeInfo(postId: Long, currentUserId: Long?): Pair<Long, Boolean> {
        val post = forumPostRepository.findById(postId).orElseThrow {
            IllegalArgumentException("Post with id $postId not found")
        }

        val isLiked = currentUserId?.let {
            postLikeRepository.existsByPostIdAndUserId(postId, it)
        } ?: false

        return Pair(postLikeRepository.countByPostId(postId), isLiked)
    }

    @Transactional
    override fun toggleLike(postId: Long, userId: Long): Pair<Long, Boolean> {
        val post = forumPostRepository.findById(postId).orElseThrow {
            IllegalArgumentException("Post with id $postId not found")
        }

        val user = userRepository.findById(userId).orElseThrow {
            IllegalArgumentException("User with id $userId not found")
        }

        val existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId)

        if (existingLike != null) {
            postLikeRepository.delete(existingLike)
            return Pair(postLikeRepository.countByPostId(postId), false)
        } else {
            val newLike = PostLike(
                post = post,
                user = user
            )
            postLikeRepository.save(newLike)
            return Pair(postLikeRepository.countByPostId(postId), true)
        }
    }

    override fun mapToDto(postLike: PostLike): PostLikeDTO {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

        return PostLikeDTO(
            id = postLike.id,
            postId = postLike.post.id!!,
            userId = postLike.user.id!!,
            username = postLike.user.email ?: "Unknown User",
            createdAt = postLike.createdAt?.format(formatter) ?: "Unknown Date"
        )
    }
}