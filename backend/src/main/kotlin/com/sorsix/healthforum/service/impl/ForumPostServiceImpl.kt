package com.sorsix.healthforum.service.impl

import com.sorsix.healthforum.model.ForumPost
import com.sorsix.healthforum.model.dto.requests_dtos.forums.CreateForumRequest
import com.sorsix.healthforum.model.dto.requests_dtos.forums.UpdateForumRequest
import com.sorsix.healthforum.model.exceptions.ForumNotFoundException
import com.sorsix.healthforum.repository.ForumPostRepository
import com.sorsix.healthforum.repository.PostLikeRepository
import com.sorsix.healthforum.service.CommentService
import com.sorsix.healthforum.service.DiseaseService
import com.sorsix.healthforum.service.ForumPostService
import com.sorsix.healthforum.service.UserService
import jakarta.transaction.Transactional
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class ForumPostServiceImpl(
    private val forumPostRepository: ForumPostRepository,
    private val userService: UserService,
    private val diseaseService: DiseaseService,
    private val postLikeRepository: PostLikeRepository,
    @Lazy
    private val commentService: CommentService,
) : ForumPostService {

    override fun getAllForumPosts(): List<ForumPost> {
        return forumPostRepository.findAllPosts()
    }

    override fun getByTitle(title: String): MutableList<ForumPost> {
        val postsWithTitle = forumPostRepository.findByPostTitle(title)
        return postsWithTitle
    }

    override fun getByForumPostId(id: Long): ForumPost? {
        return forumPostRepository.findById(id).orElseThrow { ForumNotFoundException.byId(id.toString()) }
    }

    override fun getByUserId(userId: Long): List<ForumPost> {
        return forumPostRepository.findByUserId(userId)
    }

    @Transactional
    override fun savePost(createForumRequest: CreateForumRequest): ForumPost {
        val user = userService.getUserById(createForumRequest.userId)
        val title = createForumRequest.title.trim()
        val content = createForumRequest.content.trim()

        require(title.isNotBlank()) { "Title must not be blank" }
        require(content.isNotBlank()) { "Content must not be blank" }

        val diseases = createForumRequest.diseaseIds
            .map { diseaseService.getDiseaseById(it) }
            .toMutableSet()

        val postToSave = ForumPost(
            title = title,
            content = content,
            user = user,
            diseases = diseases
        )

        return forumPostRepository.save(postToSave)
    }

    @Transactional
    override fun deletePost(id: Long) {
        if (!forumPostRepository.existsById(id)) {
            throw IllegalArgumentException("Post with id: $id not found")
        }

        deleteAllLikesForForumPost(id)
        commentService.deleteCommentsAndRepliesForPost(id)
        forumPostRepository.deleteById(id)
    }

    private fun deleteAllLikesForForumPost(postId: Long) {
        postLikeRepository.deleteByPostId(postId)
    }

    override fun findPostsByContent(content: String): List<ForumPost> {
        return forumPostRepository.findByPostContent(content)
    }

    override fun getAllPostByDiseaseId(id: Long): List<ForumPost> {
        return forumPostRepository.findByDiseaseId(id)
    }

    @Transactional
    override fun updatePost(id: Long, updateForumRequest: UpdateForumRequest): ForumPost {
        val existingPost = forumPostRepository.findById(id).orElseThrow {
            ForumNotFoundException.byId(id.toString())
        }
        val title = updateForumRequest.title.trim()
        val content = updateForumRequest.content.trim()

        require(title.isNotBlank()) { "Title must not be blank" }
        require(content.isNotBlank()) { "Content must not be blank" }

        val updatedPost = existingPost.copy(title = title, content = content)

        return forumPostRepository.save(updatedPost)

    }

    override fun getByDiseases(diseases: MutableSet<Long>): List<ForumPost> {
        if (diseases.isEmpty()) {
            return emptyList()
        }
        return forumPostRepository.findByDiseasesIdIn(diseases)
    }


}