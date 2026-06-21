package com.example.app.feature.article.ui

import com.example.app.feature.article.domain.model.Article
import com.example.app.feature.article.domain.usecase.GetArticleUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleViewModelTest {

    private val getArticleUseCase: GetArticleUseCase = mockk()
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load 성공 시 Success 상태가 된다`() = runTest(dispatcher) {
        val article = Article(
            id = "1",
            title = "title",
            author = "author",
            publishedAt = 0L,
            likeCount = 0,
            isBookmarked = false,
        )
        coEvery { getArticleUseCase("1") } returns article
        val viewModel = ArticleViewModel(getArticleUseCase)

        viewModel.load("1")
        advanceUntilIdle()

        assertEquals(ArticleUiState.Success(article), viewModel.uiState.value)
    }
}
