package com.example.app.feature.article.domain.model

data class Article(
    val id: String,
    val title: String,
    val author: String,
    val publishedAt: Long,
    val likeCount: Int,
    val isBookmarked: Boolean,
)
