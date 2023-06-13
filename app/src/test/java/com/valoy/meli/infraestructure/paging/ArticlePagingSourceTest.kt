package com.valoy.meli.infraestructure.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.valoy.meli.domain.model.Article
import com.valoy.meli.infraestructure.client.ArticleClient
import com.valoy.meli.infraestructure.dto.Paging
import com.valoy.meli.infraestructure.dto.Result
import com.valoy.meli.infraestructure.dto.SearchResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticlePagingSourceTest {

    private val articleClient = mockk<ArticleClient>()
    private lateinit var pagingSource: ArticlePagingSource

    @Before
    fun setUp() {
        pagingSource = ArticlePagingSource(QUERY, articleClient)
    }

    @Test
    fun `return error on load failure`() = runTest {
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
    fun `return first page with one item per page with one item total`() = runTest {

        coEvery { articleClient.getArticles(any(), any(), any(), any()) } returns SearchResponse(
            results = listOf(RESULT),
            paging = Paging(
                total = 1,
                offset = 0,
                limit = ITEM_PER_PAGE,
            )
        )

        val actual = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = PAGE_ZERO,
                loadSize = 45,
                placeholdersEnabled = false
            )
        )

        val expected = PagingSource.LoadResult.Page(
            data = listOf(ARTICLE),
            prevKey = null,
            nextKey = null
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `return first page with one item per page with two items total`() = runTest {

        coEvery { articleClient.getArticles(any(), any(), any(), any()) } returns SearchResponse(
            results = listOf(RESULT),
            paging = Paging(
                total = 2,
                offset = 0,
                limit = ITEM_PER_PAGE,
            )
        )

        val actual = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = PAGE_ZERO,
                loadSize = 45,
                placeholdersEnabled = false
            )
        )

        val expected = PagingSource.LoadResult.Page(
            data = listOf(ARTICLE),
            prevKey = null,
            nextKey = PAGE_ONE
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `append second page with one item per page with three items total`() = runTest {

        coEvery { articleClient.getArticles(any(), any(), any(), any()) } returns SearchResponse(
            results = listOf(RESULT),
            paging = Paging(
                total = 3,
                offset = 1,
                limit = ITEM_PER_PAGE
            )
        )

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

        assertEquals(expected, actual)
    }

    @Test
    fun `prepend first page with one item per page with three items total`() = runTest {

        coEvery { articleClient.getArticles(any(), any(), any(), any()) } returns SearchResponse(
            results = listOf(RESULT),
            paging = Paging(
                total = 3,
                offset = 1,
                limit = ITEM_PER_PAGE
            )
        )

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

        assertEquals(expected, actual)
    }


    @Test
    fun `refresh key on null anchor`() = runTest {

        val pagingState = PagingState<Int, Article>(
            emptyList(),
            null,
            PagingConfig(ITEM_PER_PAGE),
            0
        )

        val actual = pagingSource.getRefreshKey(pagingState)

        assertEquals(null, actual)
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