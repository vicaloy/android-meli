package com.valoy.meli.infraestructure.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.valoy.meli.domain.model.Article

@JsonClass(generateAdapter = true)
data class ArticleResponse(
    val id: String,
    val title: String,
    val price: Double,
    @Json(name = "currency_id") val currency: String,
    val pictures: List<Image>,
) {
    fun mapToArticle() = Article(
        id = id,
        title = title,
        thumbnails = pictures.map { it.mapToThumbnail() }
    )
}