package com.valoy.meli.infraestructure.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Paging (
    val total: Long,

    @Json(name = "primary_results")
    val primaryResults: Long? = null,

    val offset: Int,
    val limit: Int
)