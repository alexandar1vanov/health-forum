package com.sorsix.healthforum.repository

import com.sorsix.healthforum.model.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {

    @Query(
        "SELECT cm FROM Comment AS cm" +
                " WHERE cm.forumPost.id = :forumPostId" +
                " AND cm.user.isDeleted = false " +
                "ORDER BY cm.createdAt DESC"
    )
    fun findByForumPostId(@Param("forumPostId") forumPostId: Long): List<Comment>

    fun findByUserId(userId: Long): List<Comment>

    fun deleteByForumPostId(forumPostId: Long)

}