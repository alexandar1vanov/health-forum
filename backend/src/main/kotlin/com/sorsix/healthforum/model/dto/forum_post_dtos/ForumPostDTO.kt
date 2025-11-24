package com.sorsix.healthforum.model.dto.forum_post_dtos

import com.sorsix.healthforum.model.dto.users_dtos.UsersDTO

data class ForumPostDTO(
    val id: Long,
    val title: String,
    val content: String,
    val user: UsersDTO,
    val likeCount: Long=0,
    val isLikedByCurrentUser: Boolean = false
)