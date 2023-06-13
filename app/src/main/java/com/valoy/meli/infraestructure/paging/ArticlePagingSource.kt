package com.valoy.meli.infraestructure.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.valoy.meli.domain.model.Article
import com.valoy.meli.infraestructure.client.ArticleClient

class ArticlePagingSource(private val query: String, private val articleClient: ArticleClient) :
    PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>) = try {
        val offset = params.key ?: STARTING_PAGE_INDEX

        val response = articleClient.getArticles("MLA", query, offset, params.loadSize)

        LoadResult.Page(
            data = response.mapToArticles(),
            prevKey =
            if (response.paging.offset - response.paging.limit < 0) null
            else response.paging.offset - response.paging.limit,
            nextKey =
            if (response.paging.offset + response.paging.limit >= response.paging.total) null
            else response.paging.offset + response.paging.limit
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