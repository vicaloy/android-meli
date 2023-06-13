package com.valoy.meli.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.valoy.meli.ui.viewmodel.ArticleViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ArticleSearchFragment : Fragment() {

    private var _binding: FragmentArticleSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ArticleViewModel>(
        factoryProducer = { Injection.provideViewModelFactory(owner = this) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindRecyclerAdapter()
        showProgress()
        onLoadSearchedData()
        onSearch()
        onRememberSearchedQuery()
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

    private fun navigateToDetailArticle() {
        findNavController().navigate(
            ArticleSearchFragmentDirections.actionSearchArticleFragmentToDetailArticleFragment()
        )
    }

    private fun onLoadSearchedData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.articlesPaging.collectLatest { articlePaging ->
                    (binding.list.adapter as ArticleAdapter).submitData(articlePaging)
                }
            }
        }
    }

    private fun onRememberSearchedQuery() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchQuery.collectLatest {
                    binding.searchTextField.editText?.setText(it)
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
            binding.noResult.text = "No results found"
            binding.noResult.visibility = View.VISIBLE
        } else {
            binding.noResult.visibility = View.GONE
        }
    }

    private fun showErrorWarning(loadState: CombinedLoadStates, adapter: ArticleAdapter) {
        if (isErrorOnLoad(loadState) && isAdapterEmpty(adapter)) {
            binding.noResult.text = "Wops, try again"
            binding.noResult.visibility = View.VISIBLE
        } else {
            binding.noResult.visibility = View.GONE
        }
    }

    private fun showProgress() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                (binding.list.adapter as ArticleAdapter).loadStateFlow.collect {
                    binding.appendProgress.isVisible = it.source.append is LoadState.Loading
                    binding.prependProgress.isVisible = it.source.prepend is LoadState.Loading
                }
            }
        }
    }

    private fun bindRecyclerAdapter() {
        val adapter = ArticleAdapter(
            onClickListener = { article ->
                viewModel.onArticleClick(article)
                navigateToDetailArticle()
            })
        adapter.addLoadStateListener { loadState ->
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