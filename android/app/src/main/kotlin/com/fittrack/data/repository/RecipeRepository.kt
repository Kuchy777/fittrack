package com.fittrack.data.repository

import com.fittrack.data.api.FitTrackApi
import com.fittrack.data.model.RecipeRequest
import com.fittrack.data.model.RecipeResponse
import com.fittrack.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(private val api: FitTrackApi) {

    suspend fun search(query: String, tag: String?): Resource<List<RecipeResponse>> = try {
        Resource.Success(api.searchRecipes(query, tag))
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Błąd wyszukiwania przepisów")
    }

    suspend fun create(req: RecipeRequest): Resource<RecipeResponse> = try {
        Resource.Success(api.createRecipe(req))
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Błąd zapisu przepisu")
    }
}
