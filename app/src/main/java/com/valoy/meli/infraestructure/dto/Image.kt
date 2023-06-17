package com.valoy.meli.infraestructure.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Image(
    @Json(name = "secure_url") val url: String
){
    fun mapToThumbnail() = url
}