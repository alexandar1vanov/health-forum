package com.sorsix.healthforum.model.dto.requests_dtos.forums

data class CreateForumRequest(
    val title: String,
    val content: String,
    val userId: Long,
    val diseaseIds: List<Long>
)