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

    @Json(name = "catalog_product_id")
    val catalogProductID: String? = null,

    @Json(name = "listing_type_id")
    val listingTypeID: String? = null,

    val permalink: String? = null,

    @Json(name = "buying_mode")
    val buyingMode: String? = null,

    @Json(name = "site_id")
    val siteID: String? = null,

    @Json(name = "category_id")
    val categoryID: String? = null,

    @Json(name = "domain_id")
    val domainID: String? = null,

    val thumbnail: String? = null,

    @Json(name = "currency_id")
    val currencyID: String? = null,

    @Json(name = "order_backend")
    val orderBackend: Long? = null,

    val price: Double? = null,

    @Json(name = "original_price")
    val originalPrice: Double? = null,

    @Json(name = "sale_price")
    val salePrice: Double? = null,

    @Json(name = "sold_quantity")
    val soldQuantity: Long? = null,

    @Json(name = "available_quantity")
    val availableQuantity: Long? = null,

    @Json(name = "official_store_id")
    val officialStoreID: String? = null,

    @Json(name = "use_thumbnail_id")
    val useThumbnailID: Boolean? = null,

    @Json(name = "accepts_mercadopago")
    val acceptsMercadopago: Boolean? = null,

    val tags: List<String>? = null,

    @Json(name = "stop_time")
    val stopTime: String? = null,


    ){
    fun mapToArticle() = Article(
        id = id,
        title = title,
        thumbnail = thumbnail
    )
}