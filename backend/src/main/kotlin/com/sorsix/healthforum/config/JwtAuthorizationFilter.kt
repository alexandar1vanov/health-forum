package com.sorsix.healthforum.config

import com.sorsix.healthforum.utility.JWTUtility
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

class JwtAuthorizationFilter(
    private val jwtTokenUtil: JWTUtility,
    private val service: UserDetailsService,
    authManager: AuthenticationManager
) : BasicAuthenticationFilter(authManager) {
    private fun getAuthentication(token: String): UsernamePasswordAuthenticationToken? {
        if (!jwtTokenUtil.isTokenValid(token)) return null
        val email = jwtTokenUtil.getEmail(token)
        val user = service.loadUserByUsername(email)
        return UsernamePasswordAuthenticationToken(user, null, user.authorities)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val token = request.getHeader("Authorization")
        if (token == null || !token.startsWith("Bearer ")) {
            chain.doFilter(request, response)
            return
        }
        getAuthentication(token.substring(7))?.also {
            SecurityContextHolder.getContext().authentication = it
        }

        chain.doFilter(request, response)
    }


}