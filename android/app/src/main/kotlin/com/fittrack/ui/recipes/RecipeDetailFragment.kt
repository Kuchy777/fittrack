package com.fittrack.ui.recipes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.fittrack.R
import com.fittrack.databinding.FragmentRecipeDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailFragment : Fragment(R.layout.fragment_recipe_detail) {

    private val args: RecipeDetailFragmentArgs by navArgs()
    private var _b: FragmentRecipeDetailBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentRecipeDetailBinding.bind(view)

        val r = args.recipe
        b.tvTitle.text = r.title
        b.tvDescription.text = r.description ?: ""
        b.tvKcal.text  = "${r.kcalPerServing.toInt()} kcal / porcja"
        b.tvMacros.text = "B: ${r.proteinG.toInt()}g  •  T: ${r.fatG.toInt()}g  •  W: ${r.carbsG.toInt()}g"
        b.tvTime.text  = r.prepTimeMin?.let { "⏱ $it min" } ?: ""
        b.tvServings.text = "Porcji: ${r.servings}"
        b.tvTags.text = r.tags.joinToString(" · ")
        Glide.with(this).load(r.imageUrl)
            .placeholder(R.drawable.ic_recipe_placeholder)
            .into(b.ivHero)
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
