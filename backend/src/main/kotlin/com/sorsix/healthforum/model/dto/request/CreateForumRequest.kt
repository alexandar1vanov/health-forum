package com.sorsix.healthforum.model.dto.request

data class CreateForumRequest(
    val title: String,
    val content: String,
    val userId: Long,
    val diseaseIds: List<Long>
)