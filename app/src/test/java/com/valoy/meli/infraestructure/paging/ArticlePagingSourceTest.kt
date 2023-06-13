package com.valoy.meli.infraestructure.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.valoy.meli.domain.model.Article
import com.valoy.meli.infraestructure.client.ArticleClient
import com.valoy.meli.infraestructure.dto.Paging
import com.valoy.meli.infraestructure.dto.Result
import com.valoy.meli.infraestructure.dto.SearchResponse
import com.valoy.meli.utils.CoroutineMainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticlePagingSourceTest {

    private val articleClient = mockk<ArticleClient>()
    private lateinit var pagingSource: ArticlePagingSource

    @get:Rule
    val coroutineRule = CoroutineMainDispatcherRule(StandardTestDispatcher())


    @Before
    fun setUp() {
        pagingSource = ArticlePagingSource(QUERY, articleClient)
    }

    @Test
    fun `return error on load failure`() =  runTest(coroutineRule.dispatcher) {
        val error = RuntimeException("404", Throwable())
        coEvery { articleClient.getArticles(any(), any(), any(), any()) } throws error
        val expectedResult = PagingSource.LoadResult.Error<Int, Article>(error)
        assertEquals(
            expectedResult, pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = PAGE_ZERO,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `return first page with one item per page with one item total`() =  runTest(coroutineRule.dispatcher) {

        givenGetArticlesResponse(1L, 0)

        val actual = whenPagingLoadRefresh(PAGE_ZERO, 45)

        val expected = PagingSource.LoadResult.Page<Int, Article>(
            data = listOf(ARTICLE),
            prevKey = null,
            nextKey = null
        )

        thenAssertEquals(expected, actual as PagingSource.LoadResult.Page<Int, Article>)
    }

    @Test
    fun `return first page with one item per page with two items total`() =  runTest(coroutineRule.dispatcher) {

        givenGetArticlesResponse(2L, 0)

        val actual = whenPagingLoadRefresh(PAGE_ZERO, 45)

        val expected = PagingSource.LoadResult.Page(
            data = listOf(ARTICLE),
            prevKey = null,
            nextKey = PAGE_ONE
        )

        thenAssertEquals(expected, actual as PagingSource.LoadResult.Page<Int, Article>)

    }

    @Test
    fun `append second page with one item per page with three items total`() =  runTest(coroutineRule.dispatcher) {

        givenGetArticlesResponse(3L, 1)

        val actual = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = PAGE_ONE,
                loadSize = 15,
                placeholdersEnabled = false
            )
        )

        val expected = PagingSource.LoadResult.Page(
            data = listOf(ARTICLE),
            prevKey = PAGE_ZERO,
            nextKey = PAGE_TWO
        )

        thenAssertEquals(expected, actual as PagingSource.LoadResult.Page<Int, Article>)

    }

    @Test
    fun `prepend first page with one item per page with three items total`() =  runTest(coroutineRule.dispatcher) {

        givenGetArticlesResponse(3L, 1)

        val actual = pagingSource.load(
            PagingSource.LoadParams.Prepend(
                key = PAGE_ONE,
                loadSize = 15,
                placeholdersEnabled = false
            )
        )

        val expected = PagingSource.LoadResult.Page(
            data = listOf(ARTICLE),
            prevKey = PAGE_ZERO,
            nextKey = PAGE_TWO
        )

        thenAssertEquals(expected, actual as PagingSource.LoadResult.Page<Int, Article>)

    }


    @Test
    fun `refresh key on null anchor`() =  runTest(coroutineRule.dispatcher) {

        val pagingState = PagingState<Int, Article>(
            emptyList(),
            null,
            PagingConfig(ITEM_PER_PAGE),
            0
        )

        val actual = pagingSource.getRefreshKey(pagingState)

        assertEquals(null, actual)
    }

    private fun givenGetArticlesResponse(total: Long, offset: Int) {
        coEvery { articleClient.getArticles(any(), any(), any(), any()) } returns SearchResponse(
            results = listOf(RESULT),
            paging = Paging(
                total = total,
                offset = offset,
                limit = ITEM_PER_PAGE,
            )
        )
    }

    private suspend fun whenPagingLoadRefresh(key: Int, loadSize: Int) = pagingSource.load(
        PagingSource.LoadParams.Refresh(
            key = key,
            loadSize = loadSize,
            placeholdersEnabled = false
        )
    )

    private fun thenAssertEquals(
        expected: PagingSource.LoadResult.Page<Int, Article>,
        actual: PagingSource.LoadResult.Page<Int, Article>
    ) {
        assertEquals(expected, actual)
    }

    companion object {
        private const val ITEM_PER_PAGE = 1
        private const val PAGE_ZERO = 0
        private const val PAGE_ONE = 1
        private const val PAGE_TWO = 2
        private const val QUERY = "query"
        private val ARTICLE = Article(
            "id",
            "title",
            "thumbnail"
        )
        private val RESULT = Result(id = "id", title = "title", thumbnail = "thumbnail")
    }
}