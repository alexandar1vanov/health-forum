package com.sorsix.healthforum.utility

import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JWTUtility {
    @Value("\${health_forum.jwt.secret}")
    lateinit var SECRET: String

    private val EXPIRATION_MS = 2_592_000_000L

    fun generateToken(userId: Long, email: String, role: String): String {
        val now = Date()
        val expiration = Date(now.time + EXPIRATION_MS)
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("userId", userId)
            .claim("email", email)
            .claim("role", role)
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS512, SECRET.toByteArray())
            .compact()
    }

    private fun getClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .setSigningKey(SECRET.toByteArray())
                .parseClaimsJws(token).body
        } catch (e: JwtException) {
            print(e)
            null
        } catch (e: IllegalArgumentException) {
            print(e)
            null
        } catch (e: Exception) {
            print(e)
            null
        }
    }

    fun getUserId(token: String): Long = getClaims(token)?.get("userId") as Long

    fun getEmail(token: String): String = getClaims(token)?.get("email") as String

    fun isTokenValid(token: String): Boolean {
        val claims = getClaims(token)
        return if (claims != null) {
            val expirationDate = claims.expiration
            val now = Date(System.currentTimeMillis())
            now.before(expirationDate)
        } else {
            false
        }
    }

}