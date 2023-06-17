package com.valoy.meli.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.valoy.meli.databinding.ArticleViewholderBinding
import com.valoy.meli.ui.dto.ArticleDto

class ArticleViewHolder(
    private val binding: ArticleViewholderBinding,
    private val onClickListener: (ArticleDto) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(article: ArticleDto) {
        binding.apply {
            root.setOnClickListener { onClickListener(article) }
            title.text = article.title
            Glide.with(thumbnail.context)
                .load(article.thumbnail?.get(0)?.replace("http://", "https://"))
                .into(thumbnail)
        }
    }
}
