package com.sorsix.healthforum.controller

import com.sorsix.healthforum.model.Comment
import com.sorsix.healthforum.model.dto.comment_dtos.CommentDTO
import com.sorsix.healthforum.model.dto.requests_dtos.comments.CreateCommentRequest
import com.sorsix.healthforum.model.dto.requests_dtos.comments.UpdateCommentRequest
import com.sorsix.healthforum.service.CommentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/comments")
class CommentController(
    private val commentService: CommentService,
) {
    @GetMapping
    fun getAllComments(): ResponseEntity<List<CommentDTO>> {
        return ResponseEntity(commentService.getAllComments(), HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getCommentById(@PathVariable id: Long): ResponseEntity<CommentDTO> {
        return ResponseEntity(commentService.getCommentById(id), HttpStatus.OK)
    }

    @PostMapping
    fun createComment(@Valid @RequestBody commentRequest: CreateCommentRequest): ResponseEntity<Comment> {
        return ResponseEntity(commentService.saveComment(commentRequest), HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updateComment(
        @PathVariable id: Long,
        @Valid @RequestBody commentRequest: UpdateCommentRequest
    ): ResponseEntity<Comment> {
        return ResponseEntity(commentService.updateComment(id, commentRequest), HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteComment(@PathVariable id: Long): ResponseEntity<Void> {
        commentService.deleteComment(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/forumPost/{postId}")
    fun getCommentsByForumPostId(@PathVariable postId: Long): ResponseEntity<List<CommentDTO>> {
        return ResponseEntity(commentService.getCommentsByForumPostId(postId), HttpStatus.OK)
    }

    @GetMapping("/user/{userId}")
    fun getCommentsByUserId(@PathVariable userId: Long): ResponseEntity<List<CommentDTO>> {
        return ResponseEntity(commentService.getCommentsByUserId(userId), HttpStatus.OK)
    }

}