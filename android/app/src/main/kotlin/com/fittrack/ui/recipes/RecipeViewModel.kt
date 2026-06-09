package com.fittrack.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.model.RecipeResponse
import com.fittrack.data.repository.RecipeRepository
import com.fittrack.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repo: RecipeRepository
) : ViewModel() {

    private val _recipes = MutableStateFlow<Resource<List<RecipeResponse>>>(Resource.Loading)
    val recipes: StateFlow<Resource<List<RecipeResponse>>> = _recipes

    private val _activeTag = MutableStateFlow<String?>(null)
    val activeTag: StateFlow<String?> = _activeTag

    init { load() }

    fun load(query: String = "", tag: String? = null) = viewModelScope.launch {
        _activeTag.value = tag
        _recipes.value = Resource.Loading
        _recipes.value = repo.search(query, tag)
    }

    fun setTag(tag: String?) = load(tag = tag)
}
