package com.valoy.meli.ui.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.valoy.meli.domain.repository.ArticleRepository

class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: ArticleRepository
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArticleViewModel(handle, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
