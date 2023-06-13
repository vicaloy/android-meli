package com.valoy.meli.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.valoy.meli.domain.repository.ArticleRepository
import com.valoy.meli.ui.dto.ArticleDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ArticleViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ArticleRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {


    private val _articlesPaging: MutableStateFlow<PagingData<ArticleDto>> =
        MutableStateFlow(PagingData.empty())
    val articlesPaging: StateFlow<PagingData<ArticleDto>> = _articlesPaging

    val searchQuery: StateFlow<String> = savedStateHandle.getStateFlow(QUERY, "")

    val articleClicked = savedStateHandle.getStateFlow(ARTICLE, ArticleDto())

    fun onSearch(query: String) {
        savedStateHandle["query"] = query
        viewModelScope.launch(dispatcher) {
            _articlesPaging.emitAll(
                repository.getArticles(query)
                    .map { pagingData ->
                        pagingData.map { article ->
                            ArticleDto.fromArticle(article)
                        }
                    }
                    .cachedIn(viewModelScope)
            )
        }
    }

    fun onArticleClick(article: ArticleDto) {
        savedStateHandle["article"] = article
    }

    companion object {
        private const val QUERY = "query"
        private const val ARTICLE = "article"
    }
}
