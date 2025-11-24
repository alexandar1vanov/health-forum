package com.sorsix.healthforum.security

import com.sorsix.healthforum.model.enumerations.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserSecurity(
    val id: Long?,
    val email: String,
    private val password: String,
    val role: Role,
) : UserDetails {

    override fun getAuthorities() : MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword() = password

    override fun getUsername() = email

    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}