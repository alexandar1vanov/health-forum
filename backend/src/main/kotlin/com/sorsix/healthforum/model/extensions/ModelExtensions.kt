package com.sorsix.healthforum.model.extensions

import com.sorsix.healthforum.model.*
import com.sorsix.healthforum.model.dto.response.CommentDTO
import com.sorsix.healthforum.model.dto.response.DiseasesDTO
import com.sorsix.healthforum.model.dto.response.ForumPostDTO
import com.sorsix.healthforum.model.dto.response.CommentRepliedDTO
import com.sorsix.healthforum.model.dto.response.ReplyDTO
import com.sorsix.healthforum.model.dto.response.PostRatingDTO
import com.sorsix.healthforum.model.dto.response.UserPanelDTO
import com.sorsix.healthforum.model.dto.response.UsersDTO

fun User.toUsersDTO(): UsersDTO = UsersDTO(
    id = this.id,
    email = this.email
)

fun User.toUserPanelDTO(): UserPanelDTO = UserPanelDTO(
    id = this.id,
    email = this.email,
    role = this.role,
    hasSelectedDiseases = this.hasSelectedDiseases,
    createdAt = this.createdAt
)

fun ForumPost.toForumPostDTO(): ForumPostDTO = ForumPostDTO(
    id = this.id!!,
    title = this.title!!,
    content = this.content!!,
    user = this.user!!.toUsersDTO(),
    createdAt = this.createdAt!!,
    updatedAt = this.updatedAt!!,
)

fun Comment.toCommentDTO(): CommentDTO = CommentDTO(
    id = this.id!!,
    user = this.user!!.toUsersDTO(),
    forumPost = this.forumPost!!.toForumPostDTO(),
    content = this.content!!,
    createdAt = this.createdAt!!
)

fun Disease.toDiseasesDTO(): DiseasesDTO = DiseasesDTO(
    id = this.id!!,
    name = this.name!!,
    category = this.category!!,
    description = this.description!!
)

fun Comment.toCommentRepliedDTO(): CommentRepliedDTO = CommentRepliedDTO(
    id = this.id!!,
)

fun Reply.toReplyDTO(): ReplyDTO = ReplyDTO(
    id = this.id!!,
    content = this.content!!,
    user = this.user?.toUsersDTO()!!,
    comment = this.comment?.toCommentRepliedDTO()!!,
    createdAt = this.createdAt!!
)

fun PostRating.toPostRatingDTO(): PostRatingDTO = PostRatingDTO(
    id = this.id!!,
    userId = this.user?.id,
    postId = this.post?.id,
    rating = this.rating,
)
