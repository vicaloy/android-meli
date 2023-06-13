package com.valoy.meli.infraestructure.repository

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.map
import com.valoy.meli.infraestructure.client.ArticleClient
import com.valoy.meli.infraestructure.dto.Paging
import com.valoy.meli.infraestructure.dto.Result
import com.valoy.meli.infraestructure.dto.SearchResponse
import com.valoy.meli.ui.adapter.ArticleAdapter
import com.valoy.meli.ui.dto.ArticleDto
import com.valoy.meli.ui.ArticleViewModelTest
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
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
class ArticleRemoteRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val articleClient = mockk<ArticleClient>()
    private lateinit var articleRemoteRepository: ArticleRemoteRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        articleRemoteRepository = ArticleRemoteRepository(articleClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `return article paging data`() = runTest(testDispatcher) {
        givenGetArticlesResponse()

        val differ = givenAsyncPagingDataDiffer()

        val articlesPaging = whenGetArticlesFromRepo()

        val job = launch {
            articlesPaging.collectLatest { pagingData ->
                differ.submitData(pagingData)
            }
        }

        advanceUntilIdle()

        com.google.common.truth.Truth.assertThat(differ.snapshot())
            .containsExactly(ARTICLE_DTO)

        job.cancel()

    }

    @Test
    fun `return empty article paging data on error`() = runTest(testDispatcher) {
        givenGetArticlesError()

        val differ = givenAsyncPagingDataDiffer()

        val articlesPaging = whenGetArticlesFromRepo()

        val job = launch {
            articlesPaging.collectLatest { pagingData ->
                differ.submitData(pagingData)
            }
        }

        advanceUntilIdle()

        assertEquals(differ.snapshot().toList(), emptyList<ArticleDto>())

        job.cancel()

    }

    private fun givenAsyncPagingDataDiffer(): AsyncPagingDataDiffer<ArticleDto> {
        return AsyncPagingDataDiffer(
            diffCallback = ArticleAdapter.ARTICLE_DIFF_CALLBACK,
            updateCallback = ArticleViewModelTest.noopListUpdateCallback,
            mainDispatcher = testDispatcher,
            workerDispatcher = testDispatcher,
        )
    }

    private fun givenGetArticlesError() {
        coEvery { articleClient.getArticles(any(), any(), any(), any()) } throws Exception()
    }

    private fun givenGetArticlesResponse() {
        coEvery { articleClient.getArticles(any(), any(), any(), any()) } returns SearchResponse(
            results = listOf(RESULT),
            paging = Paging(
                total = 1,
                offset = 0,
                limit = ITEM_PER_PAGE
            )
        )
    }

    private fun whenGetArticlesFromRepo(): Flow<PagingData<ArticleDto>> {
        val articlesPaging = articleRemoteRepository.getArticles(QUERY)
            .map { pagingData ->
                pagingData.map { article ->
                    ArticleDto.fromArticle(article)
                }
            }
        return articlesPaging
    }

    companion object {
        private const val ITEM_PER_PAGE = 1
        private const val QUERY = "query"
        private val ARTICLE_DTO = ArticleDto(
            "id",
            "title",
            "thumbnail"
        )
        private val RESULT = Result(id = "id", title = "title", thumbnail = "thumbnail")
    }
}