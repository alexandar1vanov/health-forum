package com.sorsix.healthforum.service

import com.sorsix.healthforum.model.PostLike
import com.sorsix.healthforum.model.dto.response.PostLikeCountDTO
import com.sorsix.healthforum.model.dto.response.PostLikeDTO

interface PostLikeService {

    fun getLikesForPost(postId: Long): List<PostLikeDTO>

    fun getLikeCount(postId: Long, currentUserId: Long?): PostLikeCountDTO

    fun toggleLike(postId: Long, userId: Long): Pair<Long, Boolean>

    fun mapToDto(postLike: PostLike): PostLikeDTO

}