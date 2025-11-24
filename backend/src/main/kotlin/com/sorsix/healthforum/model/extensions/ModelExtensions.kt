import com.sorsix.healthforum.model.*
import com.sorsix.healthforum.model.dto.comment_dtos.CommentDTO
import com.sorsix.healthforum.model.dto.disease_dtos.DiseasesDTO
import com.sorsix.healthforum.model.dto.forum_post_dtos.ForumPostDTO
import com.sorsix.healthforum.model.dto.reply_dtos.CommentRepliedDTO
import com.sorsix.healthforum.model.dto.reply_dtos.ReplyDTO
import com.sorsix.healthforum.model.dto.users_dtos.UserPanelDTO
import com.sorsix.healthforum.model.dto.users_dtos.UsersDTO

fun User.toUsersDTO(): UsersDTO = UsersDTO(
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
    user = this.user!!.toUsersDTO()
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