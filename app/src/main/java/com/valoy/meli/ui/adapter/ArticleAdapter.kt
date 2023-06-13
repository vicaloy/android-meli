package com.valoy.meli.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.valoy.meli.databinding.ArticleViewholderBinding
import com.valoy.meli.ui.dto.ArticleDto

class ArticleAdapter(private val onClickListener: (ArticleDto) -> Unit) : PagingDataAdapter<ArticleDto, ArticleViewHolder>(
    ARTICLE_DIFF_CALLBACK
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder =
        ArticleViewHolder(
            ArticleViewholderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
            onClickListener
        )

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        if (article != null) {
            holder.bind(article)
        }
    }

    companion object {
        val ARTICLE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticleDto>() {
            override fun areItemsTheSame(oldItem: ArticleDto, newItem: ArticleDto): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ArticleDto, newItem: ArticleDto): Boolean =
                oldItem == newItem
        }
    }


}
