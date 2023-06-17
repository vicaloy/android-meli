package com.valoy.meli.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.valoy.meli.Injection
import com.valoy.meli.databinding.FragmentArticleDetailBinding
import com.valoy.meli.domain.model.Article
import com.valoy.meli.ui.dto.ArticleDto
import com.valoy.meli.ui.search.ArticleSearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ArticleDetailFragment : Fragment() {

    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<ArticleDetailFragmentArgs>()

    private val viewModel by viewModels<ArticleDetailViewModel>(
        factoryProducer = { Injection.provideArticleDetailViewModelFactory(owner = this) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onSearchArticle()
        onLoadArticle()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onSearchArticle(){
        viewModel.onSearch(args.articleId)
    }
    private fun onLoadArticle() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    when (uiState) {
                        is ArticleDetailViewModel.UiState.Loading -> {
                            showProgress(true)
                        }

                        is ArticleDetailViewModel.UiState.Success -> {
                            showProgress(false)
                            bindArticle(uiState.article)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun bindArticle(article: ArticleDto) {
        with(binding) {
            title.text = article.title
            Glide.with(thumbnail.context)
                .load(article.thumbnail?.get(0)?.replace("http://", "https://"))
                .into(thumbnail)
        }
    }

    private fun showProgress(show: Boolean) {
        binding.progress.isVisible = show
    }

}