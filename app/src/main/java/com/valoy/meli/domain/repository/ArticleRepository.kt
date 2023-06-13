package com.valoy.meli.domain.repository

import androidx.paging.PagingData
import com.valoy.meli.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getArticles(query: String): Flow<PagingData<Article>>
}
