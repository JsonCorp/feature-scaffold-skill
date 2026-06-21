package com.example.app.feature.article.data.mapper

import com.example.app.feature.article.data.dto.ArticleDto
import com.example.app.feature.article.domain.model.Article

fun ArticleDto.toDomain(): Article = Article(
    id = id,
    title = title,
    author = author,
    publishedAt = publishedAt,
    likeCount = likeCount,
    isBookmarked = isBookmarked,
)
