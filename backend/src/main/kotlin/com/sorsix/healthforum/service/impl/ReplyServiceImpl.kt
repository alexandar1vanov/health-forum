package com.sorsix.healthforum.service.impl

import com.sorsix.healthforum.model.Reply
import com.sorsix.healthforum.model.dto.reply_dtos.ReplyDTO
import com.sorsix.healthforum.model.dto.requests_dtos.replies.CreateReplyRequest
import com.sorsix.healthforum.model.dto.requests_dtos.replies.UpdateReplyRequest
import com.sorsix.healthforum.model.exceptions.ReplyNotFoundException
import com.sorsix.healthforum.repository.ReplyRepository
import com.sorsix.healthforum.service.CommentService
import com.sorsix.healthforum.service.ReplyService
import com.sorsix.healthforum.service.UserService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import toReplyDTO

@Service
class ReplyServiceImpl(
    private val replyRepository: ReplyRepository,
    private val userService: UserService,
    private val commentService: CommentService,
) : ReplyService {
    override fun getAllReplies(): List<Reply> {
        return replyRepository.findAll()
    }

    override fun getReplyById(id: Long): ReplyDTO {
        return replyRepository.findById(id)
            .map { it.toReplyDTO() }
            .orElseThrow { ReplyNotFoundException.byId(id.toString()) }
    }

    override fun getByUserId(userId: Long): List<Reply> {
        return replyRepository.findByUserId(userId)
    }

    @Transactional
    override fun saveReply(createReplyRequest: CreateReplyRequest): Reply {
        val user = userService.getUserById(createReplyRequest.userId)
        val comment = commentService.findCommentById(createReplyRequest.commentId)
        val content = createReplyRequest.content.trim()

        require(content.isNotBlank()) { "Reply text must not be blank" }

        val replyToSave = Reply(
            content = content,
            user = user,
            comment = comment
        )

        return replyRepository.save(replyToSave)
    }

    @Transactional
    override fun deleteReply(id: Long) {
        if (!replyRepository.existsById(id)) {
            throw IllegalArgumentException("Reply with id: $id not found")
        }
        replyRepository.deleteById(id)
    }

    @Transactional
    override fun updateReply(id: Long, updateReplyRequest: UpdateReplyRequest): Reply {
        val reply = replyRepository.findById(id).orElseThrow { ReplyNotFoundException.byId(id.toString()) }
        val content = updateReplyRequest.content.trim()
        require(content.isNotBlank()) { "Reply text must not be blank" }

        val updateReply = reply.copy(content = content)

        return replyRepository.save(updateReply)
    }

    override fun getRepliesByCommentId(commentId: Long): List<Reply> {
        return replyRepository.getRepliesByCommentId(commentId)
    }

}