package com.valoy.meli.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.valoy.meli.Injection
import com.valoy.meli.databinding.FragmentArticleSearchBinding
import com.valoy.meli.ui.adapter.ArticleAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ArticleSearchFragment : Fragment() {

    private var _binding: FragmentArticleSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ArticleSearchViewModel>(
        factoryProducer = { Injection.provideArticleSearchViewModelFactory(owner = this) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindRecyclerAdapter()
        showProgress()
        onLoadSearchedData()
        onSearch()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.onSaveState(binding.searchTextField.editText?.text.toString())
    }

    private fun navigateToDetailArticle(articleId: String) {
        val directions =
            ArticleSearchFragmentDirections.actionSearchArticleFragmentToDetailArticleFragment()
                .setArticleId(articleId)
        findNavController().navigate(directions)
    }

    private fun onLoadSearchedData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    when (uiState) {
                        is ArticleSearchViewModel.UiState.Loading -> {
                            showProgress()
                        }

                        is ArticleSearchViewModel.UiState.Success -> {
                            showProgress()
                            uiState.articles.collectLatest { articles ->
                                (binding.list.adapter as ArticleAdapter).submitData(articles)
                            }
                        }

                        is ArticleSearchViewModel.UiState.State -> {
                            binding.searchTextField.editText?.apply {
                                setText(uiState.query)
                                setSelection(uiState.query.length)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun onSearch() {
        binding.searchTextField.setEndIconOnClickListener {
            viewModel.onSearch(binding.searchTextField.editText?.text.toString())
        }
    }

    private fun isListEndReached(loadState: CombinedLoadStates) =
        (loadState.append.endOfPaginationReached ||
                loadState.prepend.endOfPaginationReached ||
                loadState.refresh.endOfPaginationReached)

    private fun isErrorOnLoad(loadState: CombinedLoadStates) =
        (loadState.refresh is LoadState.Error ||
                loadState.append is LoadState.Error ||
                loadState.prepend is LoadState.Error)

    private fun isAdapterEmpty(adapter: ArticleAdapter) = adapter.itemCount == 0

    private fun showNoResultsWarning(loadState: CombinedLoadStates, adapter: ArticleAdapter) {
        if (isListEndReached(loadState) && isAdapterEmpty(adapter)) {
            binding.warning.text = "No results found"
            binding.warning.visibility = View.VISIBLE
        }
    }

    private fun showErrorWarning(loadState: CombinedLoadStates, adapter: ArticleAdapter) {
        if (isErrorOnLoad(loadState) && isAdapterEmpty(adapter)) {
            binding.warning.text = "Wops, try again"
            binding.warning.visibility = View.VISIBLE
        }
    }

    private fun hideWarning(adapter: ArticleAdapter) {
        if (!isAdapterEmpty(adapter)) {
            binding.warning.visibility = View.GONE
        }
    }

    private fun showProgress() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                (binding.list.adapter as ArticleAdapter).loadStateFlow.collect {
                    binding.progress.isVisible = it.source.append is LoadState.Loading
                    binding.progress.isVisible = it.source.prepend is LoadState.Loading
                }
            }
        }
    }

    private fun bindRecyclerAdapter() {
        val adapter = ArticleAdapter(
            onClickListener = { article ->
                navigateToDetailArticle(article.id ?: "")
            })
        adapter.addLoadStateListener { loadState ->
            hideWarning(adapter)
            showNoResultsWarning(loadState, adapter)
            showErrorWarning(loadState, adapter)
        }

        bindRecycler(adapter)
    }

    private fun bindRecycler(adapter: ArticleAdapter) {

        with(binding) {
            list.adapter = adapter
            list.layoutManager = LinearLayoutManager(list.context)
            val decoration = DividerItemDecoration(list.context, DividerItemDecoration.VERTICAL)
            list.addItemDecoration(decoration)
        }
    }
}