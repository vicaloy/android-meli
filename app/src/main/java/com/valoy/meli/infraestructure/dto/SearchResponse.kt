package com.valoy.meli.infraestructure.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponse(
    @Json(name = "site_id")
    val siteID: String? = null,

    @Json(name = "country_default_time_zone")
    val countryDefaultTimeZone: String? = null,

    val query: String? = null,
    val paging: Paging,
    val results: List<Result>? = null,

    ) {
    fun mapToArticles() = results?.map { it.mapToArticle() } ?: emptyList()
}
