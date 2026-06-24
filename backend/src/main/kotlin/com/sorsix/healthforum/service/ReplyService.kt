package com.sorsix.healthforum.service

import com.sorsix.healthforum.model.Reply
import com.sorsix.healthforum.model.dto.response.ReplyDTO
import com.sorsix.healthforum.model.dto.request.CreateReplyRequest
import com.sorsix.healthforum.model.dto.request.UpdateReplyRequest

interface ReplyService {
    fun getAllReplies(): List<Reply>

    fun getReplyById(id: Long): ReplyDTO

    fun getByUserId(userId: Long): List<Reply>

    fun saveReply(createReplyRequest: CreateReplyRequest): Reply

    fun deleteReply(id: Long)

    fun updateReply(id:Long, updateReplyRequest: UpdateReplyRequest): Reply

    fun getRepliesByCommentId(commentId: Long): List<Reply>
}