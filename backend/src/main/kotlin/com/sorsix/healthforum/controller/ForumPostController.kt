package com.sorsix.healthforum.controller

import com.sorsix.healthforum.model.ForumPost
import com.sorsix.healthforum.model.dto.requests_dtos.forums.CreateForumRequest
import com.sorsix.healthforum.model.dto.requests_dtos.forums.UpdateForumRequest
import com.sorsix.healthforum.service.ForumPostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/forum")
class ForumPostController(
    private val forumPostService: ForumPostService
) {
    @GetMapping
    fun getAllForumPosts(): ResponseEntity<List<ForumPost>> {
        return ResponseEntity(forumPostService.getAllForumPosts(), HttpStatus.OK)
    }

    @PostMapping
    fun createForumPost(@RequestBody createForumRequest: CreateForumRequest): ResponseEntity<ForumPost> {
        return ResponseEntity(forumPostService.savePost(createForumRequest), HttpStatus.CREATED)
    }

    @PutMapping("/post/{postId}")
    fun updateForumPost(
        @PathVariable postId: Long,
        @RequestBody updateForumRequest: UpdateForumRequest
    ): ResponseEntity<ForumPost> {
        return ResponseEntity(forumPostService.updatePost(postId, updateForumRequest), HttpStatus.OK)
    }

    @GetMapping("/title/{title}")
    fun getForumPostByTitle(@PathVariable title: String): ResponseEntity<MutableList<ForumPost>> {
        return ResponseEntity(forumPostService.getByTitle(title), HttpStatus.OK)
    }

    @GetMapping("/user/{userId}")
    fun getForumPostByUserId(@PathVariable userId: Long): ResponseEntity<List<ForumPost>> {
        return ResponseEntity(forumPostService.getByUserId(userId), HttpStatus.OK)
    }

    @GetMapping("/post/{postId}")
    fun getForumPostById(@PathVariable postId: Long): ResponseEntity<ForumPost?> {
        return ResponseEntity(forumPostService.getByForumPostId(postId), HttpStatus.OK)
    }

    @GetMapping("/disease/{diseaseId}")
    fun getForumPostByDiseaseId(@PathVariable diseaseId: Long): ResponseEntity<List<ForumPost>> {
        return ResponseEntity(forumPostService.getAllPostByDiseaseId(diseaseId), HttpStatus.OK)
    }

    @GetMapping("/content/{content}")
    fun getForumPostByContent(@PathVariable content: String): ResponseEntity<List<ForumPost>> {
        return ResponseEntity(forumPostService.findPostsByContent(content), HttpStatus.OK)
    }

    @DeleteMapping("/post/{postId}")
    fun deleteForumPost(@PathVariable postId: Long): ResponseEntity<Void> {
        forumPostService.deletePost(postId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

}

