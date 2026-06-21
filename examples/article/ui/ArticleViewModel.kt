package com.example.app.feature.article.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.feature.article.domain.usecase.GetArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val getArticleUseCase: GetArticleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticleUiState>(ArticleUiState.Loading)
    val uiState: StateFlow<ArticleUiState> = _uiState.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            _uiState.value = ArticleUiState.Loading
            // runCatching은 CancellationException까지 삼켜 구조적 동시성을 깨뜨리므로 사용하지 않습니다.
            try {
                _uiState.value = ArticleUiState.Success(getArticleUseCase(id))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = ArticleUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }
}
