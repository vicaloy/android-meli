package com.valoy.meli.infraestructure.repository

import com.valoy.meli.domain.model.Article
import com.valoy.meli.infraestructure.client.ArticleClient
import com.valoy.meli.infraestructure.dto.Paging
import com.valoy.meli.infraestructure.dto.Result
import com.valoy.meli.infraestructure.dto.SearchResponse
import com.valoy.meli.utils.CoroutineMainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleRemoteRepositoryTest {

    private val articleClient = mockk<ArticleClient>()
    private lateinit var articleRemoteRepository: ArticleRemoteRepository

    @get:Rule
    val coroutineRule = CoroutineMainDispatcherRule(StandardTestDispatcher())

    @Before
    fun setUp() {
        articleRemoteRepository = ArticleRemoteRepository(articleClient)
    }

    @Test
    fun `return articles`() = runTest(coroutineRule.dispatcher) {
        givenGetArticlesResponse()

        val articles= whenGetArticlesFromRepo()

        assertEquals(listOf(ARTICLE), articles)
    }

    @Test
    fun `return empty articles on error`() = runTest(coroutineRule.dispatcher) {
        givenGetArticlesError()

        val articles = whenGetArticlesFromRepo()

        assertEquals(emptyList<Article>(), articles)

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

    private suspend fun whenGetArticlesFromRepo(): List<Article> =
        articleRemoteRepository.getArticles("MLA", QUERY, ITEM_PER_PAGE, ITEM_PER_PAGE)



    companion object {
        private const val ITEM_PER_PAGE = 1
        private const val QUERY = "query"
        private val ARTICLE = Article(
            "id",
            "title",
            listOf("thumbnail")
        )
        private val RESULT = Result(id = "id", title = "title", thumbnail = "thumbnail")
    }
}