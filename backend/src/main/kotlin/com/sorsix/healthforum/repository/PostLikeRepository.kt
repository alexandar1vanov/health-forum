package com.sorsix.healthforum.repository

import com.sorsix.healthforum.model.PostLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PostLikeRepository : JpaRepository<PostLike, Long> {
    fun findByPostIdAndUserId(postId: Long, userId: Long): PostLike?

    fun existsByPostIdAndUserId(postId: Long, userId: Long): Boolean

    @Query(
        "SELECT COUNT(l) FROM PostLike AS l" +
                " WHERE l.post.id = :postId" +
                " AND l.user.isDeleted = false "
    )
    fun countByPostId(@Param("postId") postId: Long): Long

    @Modifying
    @Query("DELETE FROM PostLike pl WHERE pl.post.id = :postId")
    fun deleteByPostId(@Param("postId") postId: Long): Int
}