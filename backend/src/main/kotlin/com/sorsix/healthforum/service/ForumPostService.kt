package com.sorsix.healthforum.service

import com.sorsix.healthforum.model.ForumPost
import com.sorsix.healthforum.model.dto.requests_dtos.forums.CreateForumRequest
import com.sorsix.healthforum.model.dto.requests_dtos.forums.UpdateForumRequest

interface ForumPostService {

    fun getAllForumPosts(): List<ForumPost>

    fun getByTitle(title: String): MutableList<ForumPost>

    fun getByForumPostId(id: Long): ForumPost?

    fun getByUserId(userId: Long): List<ForumPost>

    fun savePost(createForumRequest: CreateForumRequest): ForumPost

    fun deletePost(id: Long)

    fun findPostsByContent(content: String): List<ForumPost>

    fun getAllPostByDiseaseId(id: Long): List<ForumPost>

    fun updatePost(id: Long, updateForumRequest: UpdateForumRequest): ForumPost

    fun getByDiseases(diseases: MutableSet<Long>): List<ForumPost>
}