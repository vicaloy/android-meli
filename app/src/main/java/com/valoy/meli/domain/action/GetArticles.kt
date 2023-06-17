package com.valoy.meli.domain.action

import com.valoy.meli.domain.repository.ArticleRepository

class GetArticles(private val repository: ArticleRepository) {
    suspend operator fun invoke(
        siteId: String,
        query: String,
        offset: Int,
        limit: Int
    ) = repository.getArticles(siteId, query, offset, limit)
}