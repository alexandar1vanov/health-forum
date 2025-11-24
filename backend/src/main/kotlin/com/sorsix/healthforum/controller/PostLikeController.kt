package com.sorsix.healthforum.controller

import com.sorsix.healthforum.model.dto.post_like_dtos.PostLikeCountDTO
import com.sorsix.healthforum.service.PostLikeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/posts")
class PostLikeController(
    private val postLikeService: PostLikeService
) {
    data class LikeResponse(val likeCount: Long, val isLiked: Boolean)

    @GetMapping("/{postId}/likes")
    fun getLikeInfo(
        @PathVariable postId: Long,
        @RequestParam(required = false) userId: Long?
    ): ResponseEntity<LikeResponse> {
        val postLikeCountDTO = postLikeService.getLikeCount(postId, userId)
        return ResponseEntity.ok(
            LikeResponse(
                likeCount = postLikeCountDTO.likeCount,
                isLiked = postLikeCountDTO.isLikedByCurrentUser
            )
        )
    }

    @GetMapping("/{postId}/like-count")
    fun getLikeCount(
        @PathVariable postId: Long,
        @RequestParam(required = false) userId: Long?
    ): ResponseEntity<PostLikeCountDTO> {
        return ResponseEntity(postLikeService.getLikeCount(postId, userId), HttpStatus.OK)
    }

    @PostMapping("/{postId}/like")
    fun toggleLike(
        @PathVariable postId: Long,
        @RequestParam userId: Long
    ): ResponseEntity<LikeResponse> {
        val (likeCount, isLiked) = postLikeService.toggleLike(postId, userId)
        return ResponseEntity.ok(LikeResponse(likeCount, isLiked))
    }

}