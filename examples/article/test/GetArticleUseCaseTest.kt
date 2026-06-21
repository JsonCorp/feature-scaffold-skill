package com.example.app.feature.article.domain.usecase

import com.example.app.feature.article.domain.model.Article
import com.example.app.feature.article.domain.repository.ArticleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetArticleUseCaseTest {

    private val repository: ArticleRepository = mockk()
    private val useCase = GetArticleUseCase(repository)

    @Test
    fun `invoke는 repository 결과를 그대로 반환한다`() = runTest {
        val expected = Article(
            id = "1",
            title = "title",
            author = "author",
            publishedAt = 0L,
            likeCount = 0,
            isBookmarked = false,
        )
        coEvery { repository.getArticle("1") } returns expected

        val result = useCase("1")

        assertEquals(expected, result)
    }
}
