package com.valoy.meli.infraestructure.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.valoy.meli.infraestructure.client.ArticleClient
import com.valoy.meli.domain.repository.ArticleRepository
import com.valoy.meli.infraestructure.paging.ArticlePagingSource


class ArticleRemoteRepository(private val articleClient: ArticleClient) : ArticleRepository {

    override fun getArticles(query: String) = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ArticlePagingSource(
                query,
                articleClient
            )
        }
    ).flow

    companion object {
        private const val PAGE_SIZE = 15
    }
}