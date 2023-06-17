package com.valoy.meli.infraestructure.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.valoy.meli.domain.action.GetArticles
import com.valoy.meli.domain.model.Article
import com.valoy.meli.domain.repository.ArticleRepository
import com.valoy.meli.utils.CoroutineMainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticlePagingSourceTest {

    private val getArticles = mockk<GetArticles>()
    private lateinit var pagingSource: ArticlePagingSource

    @get:Rule
    val coroutineRule = CoroutineMainDispatcherRule(StandardTestDispatcher())


    @Before
    fun setUp() {
        pagingSource = ArticlePagingSource(QUERY, getArticles)
    }

    @Test
    fun `return error on load failure`() =  runTest(coroutineRule.dispatcher) {
        val offset = 0
        val limit = 1
        val error = RuntimeException("404", Throwable())
        coEvery { getArticles(any(), any(), any(), any()) } throws error
        val expectedResult = PagingSource.LoadResult.Error<Int, Article>(error)
        assertEquals(
            expectedResult, pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = offset,
                    loadSize = limit,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `return first page with one item per page with one item total`() =  runTest(coroutineRule.dispatcher) {

        val offset = 0
        val limit = 1

        givenGetArticleRepo()

        val actual = whenPagingLoadRefresh(offset, limit)

        val expected = PagingSource.LoadResult.Page<Int, Article>(
            data = listOf(ARTICLE),
            prevKey = null,
            nextKey = null
        )

        thenAssertEquals(expected, actual as PagingSource.LoadResult.Page<Int, Article>)
    }

    @Test
    fun `return first page with one item per page with prev page`() =  runTest(coroutineRule.dispatcher) {

        val offset =  1
        val limit = 1

        givenGetArticleRepo()

        val actual = whenPagingLoadRefresh(offset, limit)

        val expected = PagingSource.LoadResult.Page(
            data = listOf(ARTICLE),
            prevKey = 0,
            nextKey = null
        )

        thenAssertEquals(expected, actual as PagingSource.LoadResult.Page<Int, Article>)

    }

    @Test
    fun `append second page with one item per page with prev and next page`() =  runTest(coroutineRule.dispatcher) {

        val offset = 1
        val limit = 1

        givenGetArticlesRepo()

        val actual = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = offset,
                loadSize = limit,
                placeholdersEnabled = false
            )
        )

        val expected = PagingSource.LoadResult.Page(
            data = listOf(ARTICLE, ARTICLE, ARTICLE),
            prevKey = PAGE_ZERO,
            nextKey = PAGE_TWO
        )

        thenAssertEquals(expected, actual as PagingSource.LoadResult.Page<Int, Article>)

    }

    @Test
    fun `prepend first page with one item per page with three items total`() =  runTest(coroutineRule.dispatcher) {

        givenGetArticlesRepo()
        val offset = 1
        val limit = 1
        val actual = pagingSource.load(
            PagingSource.LoadParams.Prepend(
                key = offset,
                loadSize = limit,
                placeholdersEnabled = false
            )
        )

        val expected = PagingSource.LoadResult.Page(
            data = listOf(ARTICLE, ARTICLE, ARTICLE),
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
            PagingConfig(ONE_ITEM_PER_PAGE),
            0
        )

        val actual = pagingSource.getRefreshKey(pagingState)

        assertEquals(null, actual)
    }

    private fun givenGetArticleRepo() {
        coEvery { getArticles(any(), any(), any(), any()) } returns listOf(ARTICLE)
    }

    private fun givenGetArticlesRepo(){
        coEvery { getArticles(any(), any(), any(), any()) } returns listOf(ARTICLE, ARTICLE, ARTICLE)
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
        private const val ONE_ITEM_PER_PAGE = 1
        private const val PAGE_ZERO = 0
        private const val PAGE_TWO = 2
        private const val QUERY = "query"
        private val ARTICLE = Article(
            "id",
            "title",
            listOf("thumbnail")
        )
    }
}