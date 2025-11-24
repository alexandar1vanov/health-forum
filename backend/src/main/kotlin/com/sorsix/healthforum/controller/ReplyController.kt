package com.sorsix.healthforum.controller

import com.sorsix.healthforum.model.Reply
import com.sorsix.healthforum.model.dto.reply_dtos.ReplyDTO
import com.sorsix.healthforum.model.dto.requests_dtos.replies.CreateReplyRequest
import com.sorsix.healthforum.model.dto.requests_dtos.replies.UpdateReplyRequest
import com.sorsix.healthforum.service.ReplyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reply")
class ReplyController(
    private val replyService: ReplyService,
) {

    @GetMapping
    fun getAllReplies(): ResponseEntity<List<Reply>> {
        return ResponseEntity(replyService.getAllReplies(),HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getReply(@PathVariable id: Long): ResponseEntity<ReplyDTO> {
        return ResponseEntity(replyService.getReplyById(id),HttpStatus.OK)
    }

    @PostMapping
    fun createReply(@RequestBody createReplyRequest: CreateReplyRequest): ResponseEntity<Reply> {
        return ResponseEntity(replyService.saveReply(createReplyRequest), HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateReply(
        @PathVariable id: Long,
        @RequestBody updateReplyRequest: UpdateReplyRequest
    ): ResponseEntity<Reply> {
        return ResponseEntity(replyService.updateReply(id, updateReplyRequest), HttpStatus.OK)
    }

    @GetMapping("/user/{userId}")
    fun getRepliesByUserId(@PathVariable userId: Long): ResponseEntity<List<Reply>> {
        return ResponseEntity(replyService.getByUserId(userId), HttpStatus.OK)
    }

    @GetMapping("/replyId/{replyId}")
    fun getReplyById(@PathVariable replyId: Long): ResponseEntity<ReplyDTO> {
        return ResponseEntity(replyService.getReplyById(replyId), HttpStatus.OK)
    }

    @GetMapping("/comment/{commentId}")
    fun getRepliesByCommentId(@PathVariable commentId: Long): ResponseEntity<List<Reply>> {
        return ResponseEntity(replyService.getRepliesByCommentId(commentId), HttpStatus.OK)
    }

}