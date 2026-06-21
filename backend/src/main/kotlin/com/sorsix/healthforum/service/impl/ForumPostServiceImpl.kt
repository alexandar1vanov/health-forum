package com.sorsix.healthforum.service.impl

import com.sorsix.healthforum.security.SecurityUtil
import com.sorsix.healthforum.model.ForumPost
import com.sorsix.healthforum.model.dto.response.ForumPostDTO
import com.sorsix.healthforum.model.dto.request.CreateForumRequest
import com.sorsix.healthforum.model.dto.request.UpdateForumRequest
import com.sorsix.healthforum.model.dto.response.UsersDTO
import com.sorsix.healthforum.model.exceptions.ForumNotFoundException
import com.sorsix.healthforum.model.extensions.toForumPostDTO
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
    private val securityUtil: SecurityUtil,
    private val postLikeRepository: PostLikeRepository,
    @Lazy
    private val commentService: CommentService,
) : ForumPostService {

    override fun getAllForumPosts(): List<ForumPostDTO> {
        val activeUserId = securityUtil.getActiveUserId()

        val posts = forumPostRepository.findAllPosts()

        return posts.map {
            ForumPostDTO(
                id = it.id,
                title = it.title,
                content = it.content,
                user = UsersDTO(it.userId, it.userEmail),
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                likeCount = it.likeCount,
                rating = it.rating ?: 0.0,
                isLikedByCurrentUser = postLikeRepository.existsByPostIdAndUserId(it.id, activeUserId)
            )
        }
    }

    override fun getByTitle(title: String): MutableList<ForumPost> = forumPostRepository.findByPostTitle(title)

    override fun getByForumPostId(id: Long): ForumPost? = forumPostRepository.findById(id)
        .orElseThrow { ForumNotFoundException.byId(id.toString()) }

    override fun getById(id: Long): ForumPostDTO? {
        val activeUserId = securityUtil.getActiveUserId()

        val post = forumPostRepository.findByForumPostId(id)

        return post.let {
            ForumPostDTO(
                id = it.id,
                title = it.title,
                content = it.content,
                user = UsersDTO(it.userId, it.userEmail),
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                likeCount = it.likeCount,
                rating = it.rating ?: 0.0,
                isLikedByCurrentUser = postLikeRepository.existsByPostIdAndUserId(it.id, activeUserId)
            )
        }
    }

    override fun getByUserId(userId: Long): List<ForumPost> = forumPostRepository.findByUserId(userId)

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

    private fun deleteAllLikesForForumPost(postId: Long) = postLikeRepository.deleteByPostId(postId)

    override fun findPostsByContent(content: String): List<ForumPostDTO> =
        forumPostRepository.findByPostContent(content).map { it.toForumPostDTO() }

    override fun getAllPostByDiseaseId(id: Long): List<ForumPostDTO> =
        forumPostRepository.findByDiseaseId(id).map { it.toForumPostDTO() }

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