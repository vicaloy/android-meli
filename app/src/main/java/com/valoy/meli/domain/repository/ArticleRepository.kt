package com.valoy.meli.domain.repository

import androidx.paging.PagingData
import com.valoy.meli.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    suspend fun getArticles(
        siteId: String,
        query: String,
        offset: Int,
        limit: Int
    ): List<Article>

    suspend fun getArticleDetail(articleId: String): Article
}
