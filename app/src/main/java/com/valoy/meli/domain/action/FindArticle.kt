package com.valoy.meli.domain.action

import com.valoy.meli.domain.repository.ArticleRepository

class FindArticle(private val repository: ArticleRepository) {
    suspend operator fun invoke(
        articleId: String
    ) = repository.findArticle(articleId)
}