package com.sorsix.healthforum.config

import com.sorsix.healthforum.repository.UserRepository
import com.sorsix.healthforum.utility.JWTUtility
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtUtility: JWTUtility,
    private val userDetailsService: UserDetailsService,
    private val userRepository: UserRepository
) {
    private fun authManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder =
            http.getSharedObject(
                AuthenticationManagerBuilder::class.java
            )
        authenticationManagerBuilder.userDetailsService(userDetailsService)
        return authenticationManagerBuilder.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:4200")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val authenticationManager = authManager(http)
        http.cors { cors -> cors.configurationSource(corsConfigurationSource()) }
            .authenticationManager(authenticationManager)
            .addFilter(JWTAuthenticationFilter(jwtUtility, authenticationManager, userRepository))
            .addFilter(JwtAuthorizationFilter(jwtUtility, userDetailsService, authenticationManager))
            .sessionManagement { sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/signup").permitAll()
                    .anyRequest().authenticated()
            }
        return http.build()
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

}