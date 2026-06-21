package com.sorsix.healthforum.service

import com.sorsix.healthforum.model.Comment
import com.sorsix.healthforum.model.dto.response.CommentDTO
import com.sorsix.healthforum.model.dto.request.CreateCommentRequest
import com.sorsix.healthforum.model.dto.request.UpdateCommentRequest

interface CommentService {

    fun getAllComments(): List<CommentDTO>
    fun getCommentById(id: Long): CommentDTO

    fun findCommentById(id: Long): Comment


    fun saveComment(createRequest: CreateCommentRequest): Comment
    fun updateComment(id: Long, commentRequest: UpdateCommentRequest): Comment

    fun deleteComment(id: Long)
    fun deleteCommentsAndRepliesForPost(forumPostId: Long)

    fun getCommentsByForumPostId(postId: Long): List<CommentDTO>
    fun getCommentsByUserId(userId: Long): List<CommentDTO>

}