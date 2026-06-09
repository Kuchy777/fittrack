package com.fittrack.service

import com.fittrack.dto.*
import com.fittrack.entity.*
import com.fittrack.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class RecipeService(
    private val userRepo: UserRepository,
    private val recipeRepo: RecipeRepository,
    private val foodRepo: FoodProductRepository
) {
    fun search(query: String, tag: String?): List<RecipeResponse> {
        val results = if (!tag.isNullOrBlank()) recipeRepo.findPublicByTag(tag)
                      else recipeRepo.searchPublic(query)
        return results.map { it.toResponse() }
    }

    @Transactional
    fun create(email: String, req: RecipeRequest): RecipeResponse {
        val user = userRepo.findByEmail(email).orElseThrow()
        val recipe = Recipe(
            author      = user,
            title       = req.title,
            description = req.description,
            imageUrl    = req.imageUrl,
            prepTimeMin = req.prepTimeMin,
            servings    = req.servings,
            isPublic    = req.isPublic
        )
        recipe.tags.addAll(req.tags)

        var totalKcal  = BigDecimal.ZERO
        var totalProt  = BigDecimal.ZERO
        var totalFat   = BigDecimal.ZERO
        var totalCarbs = BigDecimal.ZERO

        req.ingredients.forEachIndexed { i, ing ->
            val product = foodRepo.findById(ing.productId).orElseThrow()
            val factor  = ing.quantityG.divide(BigDecimal(100))
            totalKcal  += product.kcalPer100g * factor
            totalProt  += product.proteinG    * factor
            totalFat   += product.fatG        * factor
            totalCarbs += product.carbsG      * factor
            recipe.ingredients.add(RecipeIngredient(
                recipe    = recipe,
                product   = product,
                quantityG = ing.quantityG,
                unit      = ing.unit,
                sortOrder = i
            ))
        }

        val s = BigDecimal(req.servings)
        recipe.kcalPerServing = totalKcal  / s
        recipe.proteinG       = totalProt  / s
        recipe.fatG           = totalFat   / s
        recipe.carbsG         = totalCarbs / s

        return recipeRepo.save(recipe).toResponse()
    }

    private fun Recipe.toResponse() = RecipeResponse(
        id             = id,
        title          = title,
        description    = description,
        imageUrl       = imageUrl,
        prepTimeMin    = prepTimeMin,
        servings       = servings,
        kcalPerServing = kcalPerServing,
        proteinG       = proteinG,
        fatG           = fatG,
        carbsG         = carbsG,
        tags           = tags,
        isPublic       = isPublic
    )
}
