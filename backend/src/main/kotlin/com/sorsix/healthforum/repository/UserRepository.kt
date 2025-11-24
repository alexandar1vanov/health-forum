package com.sorsix.healthforum.repository

import com.sorsix.healthforum.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String): Optional<User>

    @Query("SELECT u FROM User AS u WHERE u.isDeleted = false ORDER BY u.createdAt DESC")
    fun getActiveUsers(): List<User>

    @Query("SELECT u FROM User AS u WHERE u.isDeleted = false AND u.email ILIKE %:email%")
    fun searchUserByEmail(@Param("email") email: String): User

}