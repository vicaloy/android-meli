package com.valoy.meli.ui.search

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.valoy.meli.domain.repository.ArticleRepository

class ArticleSearchViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: ArticleRepository
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(ArticleSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArticleSearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
