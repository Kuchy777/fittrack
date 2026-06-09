package com.fittrack.ui.recipes

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fittrack.R
import com.fittrack.databinding.FragmentRecipesBinding
import com.fittrack.util.Resource
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(R.layout.fragment_recipes) {

    private val vm: RecipeViewModel by viewModels()
    private var _b: FragmentRecipesBinding? = null
    private val b get() = _b!!
    private lateinit var adapter: RecipeAdapter

    private val tags = listOf("wege", "bez_laktozy", "bezglutenowe", "< 400 kcal", "wysokobiałkowe")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentRecipesBinding.bind(view)

        adapter = RecipeAdapter { recipe ->
            val action = RecipesFragmentDirections.mainToRecipeDetail(recipe)
            findNavController().navigate(action)
        }
        b.rvRecipes.adapter = adapter
        b.rvRecipes.layoutManager = LinearLayoutManager(requireContext())

        // Filtry: ChipGroup z predefiniowanymi tagami
        tags.forEach { tag ->
            val chip = Chip(requireContext()).apply {
                text = tag
                isCheckable = true
                setOnCheckedChangeListener { _, checked ->
                    vm.setTag(if (checked) tag else null)
                }
            }
            b.chipGroupFilters.addView(chip)
        }

        // Wyszukiwarka — dynamiczna lista po 3 znakach
        b.etSearch.addTextChangedListener { editable ->
            val q = editable.toString()
            if (q.length >= 3 || q.isEmpty()) vm.load(q)
        }

        lifecycleScope.launch {
            vm.recipes.collect { state ->
                b.progressBar.isVisible = state is Resource.Loading
                if (state is Resource.Success) adapter.submitList(state.data)
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
