package com.example.app.feature.article.data.api

import com.example.app.feature.article.data.dto.ArticleDto

interface ArticleApi {
    suspend fun getArticle(id: String): ArticleDto
}
