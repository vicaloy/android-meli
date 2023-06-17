package com.valoy.meli.infraestructure.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.valoy.meli.domain.model.Article
import com.valoy.meli.domain.repository.ArticleRepository

class ArticlePagingSource(private val query: String, private val articleRepository: ArticleRepository) :
    PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>) = try {
        val offset = params.key ?: STARTING_PAGE_INDEX
        val limit = params.loadSize
        val articles = articleRepository
            .getArticles("MLA", query, offset, limit)

        LoadResult.Page(
            data = articles,
            prevKey =
            if (offset - limit < 0) null
            else offset - limit,
            nextKey =
            if (offset + limit >= articles.size) null
            else offset + limit
        )
    } catch (exception: Exception) {
        LoadResult.Error(exception)
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
       return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 0
    }

}