package com.sorsix.healthforum.model

import com.sorsix.healthforum.model.enumerations.DiseaseCategory
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "diseases")
data class Disease(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @NotBlank(message = "Disease name is required")
    @Column(name = "disease_name",nullable = false, unique = true)
    val name: String? = null,

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Disease Category is required")
    @Column(name = "disease_category", nullable = false)
    val category : DiseaseCategory? = null,

    @NotBlank(message = "Disease description is required")
    @Size(max = 1000, message = "Disease description cannot exceed 1000 characters")
    @Column(name = "disease_description", nullable = false, length = 1000)
    val description: String? = null,

    )