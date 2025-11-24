package com.sorsix.healthforum.service

import com.sorsix.healthforum.model.Comment
import com.sorsix.healthforum.model.dto.comment_dtos.CommentDTO
import com.sorsix.healthforum.model.dto.requests_dtos.comments.CreateCommentRequest
import com.sorsix.healthforum.model.dto.requests_dtos.comments.UpdateCommentRequest

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