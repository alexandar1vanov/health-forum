package com.sorsix.healthforum.controller

import com.sorsix.healthforum.model.dto.request.PostRatingRequest
import com.sorsix.healthforum.model.dto.response.PostRatingDTO
import com.sorsix.healthforum.service.PostRatingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ratings")
class PostRatingController(
    private val postRatingService: PostRatingService
) {
    @GetMapping()
    fun getRatingsByUserId(): ResponseEntity<List<PostRatingDTO>> {
        val ratings = postRatingService.getRatingsByActiveUser()
        return ResponseEntity(ratings, HttpStatus.OK)
    }

    @GetMapping("/{postId}")
    fun getRatingByPostId(
        @PathVariable postId: Long,
    ): ResponseEntity<PostRatingDTO> {
        val rating = postRatingService.getRatingByPostId(postId)
        return ResponseEntity(rating, HttpStatus.OK)
    }

    @PostMapping()
    fun submitRating(@RequestBody ratingRequest: PostRatingRequest): ResponseEntity<PostRatingDTO> {
        return ResponseEntity(postRatingService.submitRating(ratingRequest), HttpStatus.CREATED)
    }

    @DeleteMapping("/{postId}")
    fun deleteRating(@PathVariable postId: Long): ResponseEntity<Void> {
        postRatingService.deleteRating(postId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}

