package com.example.app.feature.article.domain.repository

import com.example.app.feature.article.domain.model.Article

interface ArticleRepository {
    suspend fun getArticle(id: String): Article
}
