package com.example.app.feature.article.domain.usecase

import com.example.app.feature.article.domain.model.Article
import com.example.app.feature.article.domain.repository.ArticleRepository
import javax.inject.Inject

class GetArticleUseCase @Inject constructor(
    private val repository: ArticleRepository,
) {
    suspend operator fun invoke(id: String): Article = repository.getArticle(id)
}
