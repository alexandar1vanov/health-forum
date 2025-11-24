package com.sorsix.healthforum.service

import com.sorsix.healthforum.model.PostLike
import com.sorsix.healthforum.model.dto.post_like_dtos.PostLikeCountDTO
import com.sorsix.healthforum.model.dto.post_like_dtos.PostLikeDTO

interface PostLikeService {

    fun getLikesForPost(postId: Long): List<PostLikeDTO>

    fun getLikeCount(postId: Long, currentUserId: Long?): PostLikeCountDTO

    fun toggleLike(postId: Long, userId: Long): Pair<Long, Boolean>

    fun mapToDto(postLike: PostLike): PostLikeDTO

}