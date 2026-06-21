package com.example.app.feature.article.data.repository

import com.example.app.feature.article.data.api.ArticleApi
import com.example.app.feature.article.data.mapper.toDomain
import com.example.app.feature.article.domain.model.Article
import com.example.app.feature.article.domain.repository.ArticleRepository
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val api: ArticleApi,
) : ArticleRepository {
    override suspend fun getArticle(id: String): Article = api.getArticle(id).toDomain()
}
