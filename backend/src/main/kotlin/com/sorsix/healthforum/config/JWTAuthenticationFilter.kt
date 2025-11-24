package com.sorsix.healthforum.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.sorsix.healthforum.model.User
import com.sorsix.healthforum.model.dto.auth_dtos.LoginDTO
import com.sorsix.healthforum.repository.UserRepository
import com.sorsix.healthforum.response.TokenResponse
import com.sorsix.healthforum.security.UserSecurity
import com.sorsix.healthforum.utility.JWTUtility
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*

class JWTAuthenticationFilter(
    private val jwtTokenUtil: JWTUtility,
    private val authManager: AuthenticationManager,
    private val userRepository: UserRepository
) : UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?
    ): Authentication {
        val credentials = ObjectMapper().readValue(request?.inputStream, LoginDTO::class.java)
        val auth = UsernamePasswordAuthenticationToken(
            credentials.email,
            credentials.password,
        )
        return authManager.authenticate(auth)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        val userId = (authResult?.principal as UserSecurity).id
        val email = (authResult.principal as UserSecurity).email
        val role = (authResult.principal as UserSecurity).role
        val token: String = jwtTokenUtil.generateToken(userId!!, email, role.name)

        val user: User = userRepository.findById(userId).get()
        val selectedDiseases = user.hasSelectedDiseases


        val tokenResponse = TokenResponse(token, selectedDiseases, role.name)

        val json = Gson().toJson(tokenResponse)
        response?.contentType = "application/json"
        response?.characterEncoding = "UTF-8"
        response?.addHeader("Authorization", token)
        response?.writer?.print(json)
        response?.addHeader("Access-Control-Expose-Headers", "Authorization")
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        failed: AuthenticationException?
    ) {
        val error = InvalidCredentialsError()
        response?.status = error.status
        response?.contentType = "application/json"
        response?.writer?.append(error.toString())
    }

    private data class InvalidCredentialsError(
        val timestamp: Long = Date().time,
        val status: Int = 401,
        val message: String = "Please check your email/password"
    ) {
        override fun toString(): String {
            return ObjectMapper().writeValueAsString(this)
        }
    }

}