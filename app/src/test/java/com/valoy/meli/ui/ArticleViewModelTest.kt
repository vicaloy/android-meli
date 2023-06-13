package com.valoy.meli.ui

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.valoy.meli.domain.model.Article
import com.valoy.meli.domain.repository.ArticleRepository
import com.valoy.meli.ui.adapter.ArticleAdapter.Companion.ARTICLE_DIFF_CALLBACK
import com.valoy.meli.ui.dto.ArticleDto
import com.valoy.meli.ui.viewmodel.ArticleViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val articleRepository = mockk<ArticleRepository>(relaxed = true)
    private val savedStateHandle = mockk<androidx.lifecycle.SavedStateHandle>(relaxed = true)
    private lateinit var viewModel: ArticleViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ArticleViewModel(savedStateHandle, articleRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `return articles pages on search`() =  runTest(testDispatcher) {
        givenArticles()

        val differ = givenAsyncPagingDataDiffer()

        whenSearchQuery()

        val job = launch {
            viewModel.articlesPaging.collectLatest { pagingData ->
                differ.submitData(pagingData)
            }
        }

        advanceUntilIdle()

        com.google.common.truth.Truth.assertThat(differ.snapshot())
            .containsExactly(ARTICLE_DTO)

        job.cancel()
    }

    @Test
    fun `save query on search`() {
        whenSearchQuery()

        verify { savedStateHandle[QUERY] = QUERY }
    }

    @Test
    fun `save article on click`() {
        whenArticleClick()

        verify { savedStateHandle[ARTICLE_KEY] = ARTICLE_DTO }
    }

    private fun givenArticles() {
        val pagingData = PagingData.from(
            listOf(
                ARTICLE
            )
        )
        every { articleRepository.getArticles(QUERY) } returns MutableStateFlow(pagingData)
    }

    private fun givenAsyncPagingDataDiffer(): AsyncPagingDataDiffer<ArticleDto> {
        return AsyncPagingDataDiffer(
            diffCallback = ARTICLE_DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = testDispatcher,
            workerDispatcher = testDispatcher,
        )
    }

    private fun whenSearchQuery() {
        viewModel.onSearch(QUERY)
    }

    private fun whenArticleClick() {
        viewModel.onArticleClick(ARTICLE_DTO)
    }

    companion object {
        private const val ARTICLE_KEY = "article"
        private val ARTICLE = Article(
            "id",
            "title",
            "thumbnail"
        )
        private val ARTICLE_DTO = ArticleDto(
            "id",
            "title",
            "thumbnail"
        )
        private const val QUERY = "query"

        val noopListUpdateCallback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {}
            override fun onRemoved(position: Int, count: Int) {}
            override fun onMoved(fromPosition: Int, toPosition: Int) {}
            override fun onChanged(position: Int, count: Int, payload: Any?) {}
        }
    }
}