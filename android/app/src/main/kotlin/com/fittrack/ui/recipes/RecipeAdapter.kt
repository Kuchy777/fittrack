package com.fittrack.ui.recipes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fittrack.data.model.RecipeResponse
import com.fittrack.databinding.ItemRecipeBinding

class RecipeAdapter(
    private val onClick: (RecipeResponse) -> Unit
) : ListAdapter<RecipeResponse, RecipeAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemRecipeBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(recipe: RecipeResponse) {
            b.tvTitle.text  = recipe.title
            b.tvKcal.text   = "${recipe.kcalPerServing.toInt()} kcal / porcja"
            b.tvTime.text   = recipe.prepTimeMin?.let { "⏱ $it min" } ?: ""
            b.tvTags.text   = recipe.tags.joinToString(" · ")
            Glide.with(b.root).load(recipe.imageUrl)
                .placeholder(com.fittrack.R.drawable.ic_recipe_placeholder)
                .into(b.ivThumbnail)
            b.root.setOnClickListener { onClick(recipe) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<RecipeResponse>() {
            override fun areItemsTheSame(a: RecipeResponse, b: RecipeResponse) = a.id == b.id
            override fun areContentsTheSame(a: RecipeResponse, b: RecipeResponse) = a == b
        }
    }
}
