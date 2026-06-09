package com.fittrack.controller

import com.fittrack.dto.*
import com.fittrack.service.RecipeService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@Tag(name = "Recipes", description = "Niskokaloryczne przepisy")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/recipes")
class RecipeController(private val recipeService: RecipeService) {

    @GetMapping
    fun search(
        @RequestParam(defaultValue = "") q: String,
        @RequestParam(required = false) tag: String?
    ) = recipeService.search(q, tag)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@AuthenticationPrincipal ud: UserDetails, @Valid @RequestBody req: RecipeRequest) =
        recipeService.create(ud.username, req)
}
