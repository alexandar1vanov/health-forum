package com.sorsix.healthforum.controller

import com.sorsix.healthforum.model.Disease
import com.sorsix.healthforum.service.DiseaseService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/diseases")
class DiseaseController(
    private val diseaseService: DiseaseService
) {

    @GetMapping
    fun getDiseases(): ResponseEntity<List<Disease>> {
        return ResponseEntity(diseaseService.getAllDiseases(), HttpStatus.OK)
    }


    @GetMapping("/{id}")
    fun getDiseaseById(@PathVariable id: Long): ResponseEntity<Disease> {
        return ResponseEntity(diseaseService.getDiseaseById(id), HttpStatus.OK)
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    fun createDisease(
        @RequestBody
        @Valid disease: Disease
    ): ResponseEntity<Disease> {
        return ResponseEntity(diseaseService.saveDisease(disease), HttpStatus.CREATED)
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteDisease(@PathVariable id: Long): ResponseEntity<Void> {
        diseaseService.deleteDisease(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}