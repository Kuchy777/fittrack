package com.fittrack.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

// -- Auth -------------------------------------------------
@JsonClass(generateAdapter = true)
data class RegisterRequest(val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class LoginRequest(val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class RefreshRequest(val refreshToken: String)

@JsonClass(generateAdapter = true)
data class TokenResponse(val accessToken: String, val refreshToken: String)

// -- Profile ----------------------------------------------
@JsonClass(generateAdapter = true)
data class ProfileResponse(
    val displayName: String?  = null,
    val gender: String?       = null,
    val birthDate: String?    = null,
    val weightKg: Double?     = null,
    val heightCm: Double?     = null,
    val activityLevel: String?= null,
    val goal: String?         = null,
    val dailyKcalGoal: Int?   = null,
    val avatarUrl: String?    = null
)

@JsonClass(generateAdapter = true)
data class ProfileUpdateRequest(
    val displayName: String?  = null,
    val gender: String?       = null,
    val birthDate: String?    = null,
    val weightKg: Double?     = null,
    val heightCm: Double?     = null,
    val activityLevel: String?= null,
    val goal: String?         = null
)

// -- Food -------------------------------------------------
@JsonClass(generateAdapter = true)
data class FoodProductDto(
    val id: Long,
    val name: String,
    val kcalPer100g: Double = 0.0,
    val proteinG: Double  = 0.0,
    val fatG: Double      = 0.0,
    val carbsG: Double    = 0.0
)

// -- Diary ------------------------------------------------
@JsonClass(generateAdapter = true)
data class DiaryEntryRequest(
    val entryDate: String,
    val mealType: String,
    val productId: Long? = null,
    val recipeId: Long?  = null,
    val customName: String? = null,
    val quantityG: Double,
    val photoPath: String?  = null,
    val note: String?       = null,
    val synced: Boolean     = true
)

@JsonClass(generateAdapter = true)
data class DiaryEntryResponse(
    val id: Long,
    val entryDate: String,
    val mealType: String,
    val productName: String? = null,
    val quantityG: Double = 0.0,
    val kcal: Double      = 0.0,
    val proteinG: Double  = 0.0,
    val fatG: Double      = 0.0,
    val carbsG: Double    = 0.0,
    val photoPath: String? = null,
    val note: String?      = null
)

@JsonClass(generateAdapter = true)
data class DailySummaryResponse(
    val date: String,
    val kcalGoal: Int = 2000,
    val kcalConsumed: Double = 0.0,
    val kcalBurned: Int = 0,
    val kcalRemaining: Double = 0.0,
    val proteinG: Double = 0.0,
    val fatG: Double = 0.0,
    val carbsG: Double = 0.0
)

// -- Recipes ----------------------------------------------
@Parcelize
@JsonClass(generateAdapter = true)
data class RecipeResponse(
    val id: Long,
    val title: String,
    val description: String? = null,
    val imageUrl: String?    = null,
    val prepTimeMin: Int?    = null,
    val servings: Int        = 1,
    val kcalPerServing: Double = 0.0,
    val proteinG: Double = 0.0,
    val fatG: Double     = 0.0,
    val carbsG: Double   = 0.0,
    val tags: List<String> = emptyList(),
    val isPublic: Boolean  = true
) : Parcelable

@JsonClass(generateAdapter = true)
data class RecipeIngredientDto(
    val productId: Long,
    val quantityG: Double,
    val unit: String? = null
)

@JsonClass(generateAdapter = true)
data class RecipeRequest(
    val title: String,
    val description: String? = null,
    val imageUrl: String?    = null,
    val prepTimeMin: Int?    = null,
    val servings: Int        = 1,
    val isPublic: Boolean    = true,
    val tags: List<String>   = emptyList(),
    val ingredients: List<RecipeIngredientDto> = emptyList()
)

// -- Workouts ---------------------------------------------
@JsonClass(generateAdapter = true)
data class WorkoutRequest(
    val activityDate: String,
    val activityType: String,
    val durationMin: Int,
    val kcalBurned: Int,
    val distanceKm: Double? = null,
    val avgHeartRate: Int?  = null,
    val notes: String?      = null
)

@JsonClass(generateAdapter = true)
data class WorkoutResponse(
    val id: Long,
    val activityDate: String,
    val activityType: String,
    val durationMin: Int,
    val kcalBurned: Int,
    val distanceKm: Double? = null,
    val notes: String?      = null
)

// -- Notifications (lokalne) ------------------------------
@JsonClass(generateAdapter = true)
data class NotificationSettingsRequest(
    val waterReminders: Boolean = true,
    val mealReminders: Boolean  = true,
    val workoutReminders: Boolean = true
)
