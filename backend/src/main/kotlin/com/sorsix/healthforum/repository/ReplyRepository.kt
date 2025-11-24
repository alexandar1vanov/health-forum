package com.sorsix.healthforum.repository

import com.sorsix.healthforum.model.Reply
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ReplyRepository : JpaRepository<Reply, Long> {

    @Query("SELECT r FROM Reply r WHERE r.user.id = :userId")
    fun findByUserId(@Param("userId") userId: Long): List<Reply>

    @Query(
        "SELECT r FROM Reply AS r" +
                " WHERE r.comment.id = :commentId" +
                " AND r.user.isDeleted = false "
    )
    fun getRepliesByCommentId(@Param("commentId") commentId: Long): List<Reply>

    fun deleteByCommentId(commentId: Long)
    fun deleteByCommentIdIn(commentIds: List<Long>)
}