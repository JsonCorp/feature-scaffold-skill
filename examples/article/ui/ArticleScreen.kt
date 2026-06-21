package com.example.app.feature.article.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ArticleScreen(viewModel: ArticleViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val s = state) {
        is ArticleUiState.Loading -> { /* 로딩 UI */ }
        is ArticleUiState.Success -> { /* s.article 렌더링 (title, author, likeCount 등) */ }
        is ArticleUiState.Error -> { /* s.message 표시 */ }
    }
}
