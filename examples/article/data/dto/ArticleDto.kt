package com.example.app.feature.article.data.dto

data class ArticleDto(
    val id: String,
    val title: String,
    val author: String,
    val publishedAt: Long,
    val likeCount: Int,
    val isBookmarked: Boolean,
)
