package com.sorsix.healthforum.model

import com.sorsix.healthforum.model.enumerations.Role
import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    var email: String = "",

    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=\\S+$).{8,}$",
        message = "Password must be at least 8 characters, contain at least one uppercase letter, and no spaces")
    var password: String = "",

    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER,

    @Column(name = "has_selected_diseases", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    var hasSelectedDiseases: Boolean = false,

    @CreationTimestamp
    val createdAt: Date? = null,

    @UpdateTimestamp
    var updatedAt: Date? = null,

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    var isDeleted: Boolean = false
)