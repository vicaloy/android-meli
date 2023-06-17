package com.valoy.meli.ui.detail

import com.valoy.meli.domain.model.Article
import com.valoy.meli.domain.repository.ArticleRepository
import com.valoy.meli.ui.dto.ArticleDto
import com.valoy.meli.utils.CoroutineMainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleDetailViewModelTest {

    private val articleRepository = mockk<ArticleRepository>(relaxed = true)
    private lateinit var viewModel: ArticleDetailViewModel

    @get:Rule
    val coroutineRule = CoroutineMainDispatcherRule(StandardTestDispatcher())

    @Before
    fun setUp() {
        viewModel = ArticleDetailViewModel(
            articleRepository,
            coroutineRule.dispatcher
        )
    }

    @Test
    fun `return articles pages on search`() = runTest(coroutineRule.dispatcher) {
        givenArticles()

        whenSearchQuery()

        val job = launch {
            viewModel.uiState.collectLatest { uiState ->
                if(uiState is ArticleDetailViewModel.UiState.Success) {
                    assertEquals(uiState.article, ARTICLE_DTO)
                }
            }
        }

        advanceUntilIdle()


        job.cancel()
    }


    private fun givenArticles() {
        coEvery {
            articleRepository.getArticles(
                any(),
                ARTICLE_ID,
                any(),
                any()
            )
        } returns listOf(ARTICLE)
    }


    private fun whenSearchQuery() {
        viewModel.onSearch(ARTICLE_ID)
    }

    companion object {
        private val ARTICLE = Article(
            "id",
            "title",
            listOf("thumbnail")
        )
        private val ARTICLE_DTO = ArticleDto(
            "id",
            "title",
            listOf("thumbnail")

        )
        private const val ARTICLE_ID = "id"
    }
}