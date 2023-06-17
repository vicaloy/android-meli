package com.valoy.meli.infraestructure.dto

import com.valoy.meli.domain.model.Article
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Result(
    val id: String? = null,
    val title: String? = null,
    val condition: String? = null,

    @Json(name = "thumbnail_id")
    val thumbnailID: String? = null,

    @Json(name = "site_id")
    val siteID: String? = null,

    @Json(name = "category_id")
    val categoryID: String? = null,

    val thumbnail: String? = null,

    val price: Double? = null,

    @Json(name = "original_price")
    val originalPrice: Double? = null,

    @Json(name = "sale_price")
    val salePrice: Double? = null,


    ){
    fun mapToArticle() = Article(
        id = id,
        title = title,
        thumbnails = listOf(thumbnail ?: "")
    )
}