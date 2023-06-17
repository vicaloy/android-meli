package com.valoy.meli.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.valoy.meli.domain.repository.ArticleRepository
import com.valoy.meli.infraestructure.paging.ArticlePagingSource
import com.valoy.meli.ui.dto.ArticleDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ArticleSearchViewModel(
    private val repository: ArticleRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {


    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onSearch(query: String) {
        _uiState.value = UiState.Loading

        val flow = getPagingData(query)

        viewModelScope.launch(dispatcher) {
            try {
                _uiState.value = UiState.Success(flow)

            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) { }
        }
    }

    fun onSaveState(query: String) {
        _uiState.value = UiState.State(query)
    }

    private fun getPagingData(query: String): Flow<PagingData<ArticleDto>> {
        return Pager(
            PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)
        ) {
            ArticlePagingSource(query, repository)
        }.flow
            .map { pagingData ->
                pagingData.map { article ->
                    ArticleDto.fromArticle(article)
                }
            }
            .cachedIn(viewModelScope)
    }

    sealed class UiState {
        object Initial : UiState()
        object Loading : UiState()
        data class Success(val articles: Flow<PagingData<ArticleDto>>) : UiState()
        data class State(val query: String) : UiState()
    }

    companion object {
        private const val PAGE_SIZE = 15
    }
}
