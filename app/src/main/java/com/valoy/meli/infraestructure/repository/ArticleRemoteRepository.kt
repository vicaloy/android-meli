package com.valoy.meli.infraestructure.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.valoy.meli.domain.model.Article
import com.valoy.meli.infraestructure.client.ArticleClient
import com.valoy.meli.domain.repository.ArticleRepository
import com.valoy.meli.infraestructure.paging.ArticlePagingSource


class ArticleRemoteRepository(private val articleClient: ArticleClient) : ArticleRepository {
    override suspend fun getArticles(siteId: String, query: String, offset: Int, limit: Int) =
        articleClient.getArticles(siteId, query, offset, limit).mapToArticles()

    override suspend fun getArticleDetail(articleId: String): Article =
        articleClient.getArticleDetail(articleId).mapToArticle()


}