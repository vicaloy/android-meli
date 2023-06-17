package com.valoy.meli.ui.search

import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.ListUpdateCallback
import com.google.common.truth.Truth.assertThat
import com.valoy.meli.domain.action.GetArticles
import com.valoy.meli.domain.model.Article
import com.valoy.meli.ui.adapter.ArticleAdapter.Companion.ARTICLE_DIFF_CALLBACK
import com.valoy.meli.ui.dto.ArticleDto
import com.valoy.meli.utils.CoroutineMainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
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
class ArticleSearchViewModelTest {

    private val getArticles = mockk<GetArticles>()
    private lateinit var viewModel: ArticleSearchViewModel

    @get:Rule
    val coroutineRule = CoroutineMainDispatcherRule(StandardTestDispatcher())

    @Before
    fun setUp() {
        viewModel = ArticleSearchViewModel(
            getArticles,
            coroutineRule.dispatcher
        )
    }

    @Test
    fun `return articles pages on search`() = runTest(coroutineRule.dispatcher) {
        givenArticles()

        val differ = givenAsyncPagingDataDiffer()

        whenSearchQuery()

        val job = launch {
            viewModel.uiState.collectLatest { uiState ->
                if(uiState is ArticleSearchViewModel.UiState.Success) {
                    uiState.articles.collectLatest { pagingData->
                        differ.submitData(pagingData)
                    }
                }
            }
        }

        advanceUntilIdle()

        assertThat(differ.snapshot())
            .containsExactly(ARTICLE_DTO)

        job.cancel()
    }

    @Test
    fun `on save state`() = runTest(coroutineRule.dispatcher) {
        whenSavedState(QUERY)

        val job = launch {
            viewModel.uiState.collectLatest { uiState ->
                if(uiState is ArticleSearchViewModel.UiState.State) {
                    assertThat(uiState.query).isEqualTo(QUERY)
                }
            }
        }

        job.cancel()
    }

    private fun givenArticles() {
        coEvery {
            getArticles(
                any(),
                QUERY,
                any(),
                any()
            )
        } returns listOf(ARTICLE)
    }


    private fun givenAsyncPagingDataDiffer(): AsyncPagingDataDiffer<ArticleDto> {
        return AsyncPagingDataDiffer(
            diffCallback = ARTICLE_DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutineRule.dispatcher,
            workerDispatcher = coroutineRule.dispatcher,
        )
    }

    private fun whenSearchQuery() {
        viewModel.onSearch(QUERY)
    }

    private fun whenSavedState(query: String) {
        viewModel.onSaveState(query)
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
        private const val QUERY = "query"

        val noopListUpdateCallback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {}
            override fun onRemoved(position: Int, count: Int) {}
            override fun onMoved(fromPosition: Int, toPosition: Int) {}
            override fun onChanged(position: Int, count: Int, payload: Any?) {}
        }
    }
}