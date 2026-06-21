package com.sorsix.healthforum.service

import com.sorsix.healthforum.model.ForumPost
import com.sorsix.healthforum.model.dto.response.ForumPostDTO
import com.sorsix.healthforum.model.dto.request.CreateForumRequest
import com.sorsix.healthforum.model.dto.request.UpdateForumRequest

interface ForumPostService {

    fun getAllForumPosts(): List<ForumPostDTO>

    fun getByTitle(title: String): MutableList<ForumPost>

    fun getByForumPostId(id: Long): ForumPost?

    fun getById(id: Long): ForumPostDTO?

    fun getByUserId(userId: Long): List<ForumPost>

    fun savePost(createForumRequest: CreateForumRequest): ForumPost

    fun deletePost(id: Long)

    fun findPostsByContent(content: String): List<ForumPostDTO>

    fun getAllPostByDiseaseId(id: Long): List<ForumPostDTO>

    fun updatePost(id: Long, updateForumRequest: UpdateForumRequest): ForumPost

    fun getByDiseases(diseases: MutableSet<Long>): List<ForumPost>
}