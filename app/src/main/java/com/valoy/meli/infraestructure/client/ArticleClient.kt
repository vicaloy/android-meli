package com.valoy.meli.infraestructure.client

import com.valoy.meli.infraestructure.dto.ArticleResponse
import com.valoy.meli.infraestructure.dto.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticleClient {

    @GET("sites/{siteId}/search")
    suspend fun getArticles(
        @Path("siteId") siteId: String,
        @Query("q") query: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): SearchResponse

    @GET("items/{articleId}")
    suspend fun getArticleDetail(
        @Path("articleId") articleId: String
    ): ArticleResponse
}