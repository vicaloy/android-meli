package com.valoy.meli.domain.repository

import com.valoy.meli.domain.model.Article

interface ArticleRepository {
    suspend fun getArticles(
        siteId: String,
        query: String,
        offset: Int,
        limit: Int
    ): List<Article>

    suspend fun findArticle(articleId: String): Article
}
