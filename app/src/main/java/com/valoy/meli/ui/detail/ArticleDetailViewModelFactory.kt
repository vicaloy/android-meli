package com.valoy.meli.ui.detail

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.valoy.meli.domain.action.FindArticle
import com.valoy.meli.domain.repository.ArticleRepository

class ArticleDetailViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val findArticle: FindArticle
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(ArticleDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArticleDetailViewModel(findArticle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
