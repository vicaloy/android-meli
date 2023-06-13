package com.valoy.meli.ui.dto

import android.os.Parcelable
import com.valoy.meli.domain.model.Article
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ArticleDto(
    val id: String? = null,
    val title: String? = null,
    val thumbnail: String? = null
) : Parcelable {
    companion object {
        fun fromArticle(article: Article) = ArticleDto(
            id = article.id,
            title = article.title,
            thumbnail = article.thumbnail
        )
    }
}