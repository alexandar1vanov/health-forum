package com.sorsix.healthforum.service.impl

import com.sorsix.healthforum.model.Comment
import com.sorsix.healthforum.model.dto.comment_dtos.CommentDTO
import com.sorsix.healthforum.model.dto.requests_dtos.comments.CreateCommentRequest
import com.sorsix.healthforum.model.dto.requests_dtos.comments.UpdateCommentRequest
import com.sorsix.healthforum.model.exceptions.CommentNotFoundException
import com.sorsix.healthforum.repository.CommentRepository
import com.sorsix.healthforum.repository.ReplyRepository
import com.sorsix.healthforum.service.CommentService
import com.sorsix.healthforum.service.ForumPostService
import com.sorsix.healthforum.service.UserService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import toCommentDTO

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val userService: UserService,
    private val forumPostService: ForumPostService,
    private val replyRepository: ReplyRepository,
) : CommentService {

    override fun getAllComments(): List<CommentDTO> {
        return commentRepository.findAll().map {
            it.toCommentDTO()
        }
    }

    override fun getCommentById(id: Long): CommentDTO {
        return commentRepository.findById(id).map {
            it.toCommentDTO()
        }.orElseThrow { CommentNotFoundException.byId(id.toString()) }
    }

    override fun findCommentById(id: Long): Comment {
        return commentRepository.findById(id).orElseThrow {
            CommentNotFoundException.byId(id.toString())
        }
    }

    @Transactional
    override fun saveComment(createRequest: CreateCommentRequest): Comment {
        val user = userService.getUserById(createRequest.userId)
        val forumPost = forumPostService.getByForumPostId(createRequest.forumPostId)
        val content = createRequest.content.trim()
        require(content.isNotBlank()) { "Comment content must not be blank" }
        val commentToSave = Comment(
            user = user,
            forumPost = forumPost,
            content = content,
        )
        return commentRepository.save(commentToSave)
    }

    @Transactional
    override fun updateComment(id: Long, commentRequest: UpdateCommentRequest): Comment {
        val existingComment = commentRepository.findById(id)
            .orElseThrow { CommentNotFoundException.byId(id.toString()) }

        val content = commentRequest.content.trim()
        require(content.isNotBlank()) { "Comment content must not be blank" }

        val updatedComment = existingComment.copy(content = content)
        return commentRepository.save(updatedComment)
    }

    @Transactional
    override fun deleteComment(id: Long) {
        if (!commentRepository.existsById(id)) {
            throw CommentNotFoundException.byId(id.toString())
        }
        replyRepository.deleteByCommentId(id)
        commentRepository.deleteById(id)
    }

    @Transactional
    override fun deleteCommentsAndRepliesForPost(forumPostId: Long) {
        val commentsToDelete = commentRepository.findByForumPostId(forumPostId)
        if (commentsToDelete.isNotEmpty()) {
            val commentIds = commentsToDelete.mapNotNull { it.id }
            if (commentIds.isNotEmpty()) {
                replyRepository.deleteByCommentIdIn(commentIds)
            }
            commentRepository.deleteByForumPostId(forumPostId)
        }
    }


    override fun getCommentsByForumPostId(postId: Long): List<CommentDTO> {
        return commentRepository.findByForumPostId(postId)
            .map { it.toCommentDTO() }
    }

    override fun getCommentsByUserId(userId: Long): List<CommentDTO> {
        return commentRepository.findByUserId(userId)
            .map { it.toCommentDTO() }
    }

}