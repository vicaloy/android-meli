package com.valoy.meli.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valoy.meli.domain.repository.ArticleRepository
import com.valoy.meli.ui.dto.ArticleDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArticleDetailViewModel(
    private val repository: ArticleRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onSearch(articleId: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch(dispatcher) {
            try {
                val article = ArticleDto.fromArticle(repository.getArticleDetail(articleId))
                _uiState.value = UiState.Success(article)
            }catch (exception: CancellationException){
                throw exception
            }catch (_: Exception){ }

        }
    }

    sealed class UiState {
        object Initial : UiState()
        object Loading : UiState()
        data class Success(val article: ArticleDto) : UiState()
    }
}