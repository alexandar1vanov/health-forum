package com.sorsix.healthforum.repository

import com.sorsix.healthforum.model.ForumPost
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ForumPostRepository : JpaRepository<ForumPost, Long> {

    @Query(
        "SELECT fp FROM ForumPost fp " +
                "WHERE fp.title ilike %:title%" +
                " AND fp.user.isDeleted = false " +
                "ORDER BY fp.createdAt DESC"
    )
    fun findByPostTitle(@Param("title") title: String): MutableList<ForumPost>

    @Query(
        "SELECT fp FROM ForumPost AS fp " +
                "WHERE fp.content ilike %:content% " +
                "AND fp.user.isDeleted = false " +
                "ORDER BY fp.createdAt DESC"
    )
    fun findByPostContent(@Param("content") content: String): List<ForumPost>

    fun findByUserId(userId: Long): List<ForumPost>

    @Query(
        "SELECT fp FROM ForumPost fp " +
                "WHERE fp.user.isDeleted = false " +
                "ORDER BY fp.createdAt DESC"
    )
    fun findAllPosts(): List<ForumPost>

    @Query(
        "SELECT FP FROM ForumPost AS FP " +
                "JOIN FP.diseases as d" +
                " WHERE d.id = :diseaseId" +
                "   AND FP.user.isDeleted = false " +
                "ORDER BY FP.createdAt DESC"
    )
    fun findByDiseaseId(@Param("diseaseId") diseaseId: Long): List<ForumPost>

    fun findByDiseasesIdIn(diseasesIds: MutableCollection<Long>): MutableList<ForumPost>
}