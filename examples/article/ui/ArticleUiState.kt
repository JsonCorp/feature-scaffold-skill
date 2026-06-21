package com.example.app.feature.article.ui

import com.example.app.feature.article.domain.model.Article

sealed interface ArticleUiState {
    data object Loading : ArticleUiState
    data class Success(val article: Article) : ArticleUiState
    data class Error(val message: String) : ArticleUiState
}
