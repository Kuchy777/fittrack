package com.fittrack.data.api

import com.fittrack.data.model.*
import retrofit2.http.*

interface FitTrackApi {

    // ── Auth ─────────────────────────────────────────
    @POST("api/auth/register")
    suspend fun register(@Body req: RegisterRequest): TokenResponse

    @POST("api/auth/login")
    suspend fun login(@Body req: LoginRequest): TokenResponse

    @POST("api/auth/refresh")
    suspend fun refresh(@Body req: RefreshRequest): TokenResponse

    // ── Profile ──────────────────────────────────────
    @GET("api/profile")
    suspend fun getProfile(): ProfileResponse

    @PUT("api/profile")
    suspend fun updateProfile(@Body req: ProfileUpdateRequest): ProfileResponse

    // ── Diary ────────────────────────────────────────
    @GET("api/diary")
    suspend fun getDiaryEntries(@Query("date") date: String): List<DiaryEntryResponse>

    @POST("api/diary")
    suspend fun addDiaryEntry(@Body req: DiaryEntryRequest): DiaryEntryResponse

    @DELETE("api/diary/{id}")
    suspend fun deleteDiaryEntry(@Path("id") id: Long)

    @GET("api/diary/summary")
    suspend fun getDailySummary(@Query("date") date: String): DailySummaryResponse

    // ── Food ─────────────────────────────────────────
    @GET("api/food/search")
    suspend fun searchFood(@Query("q") query: String): List<FoodProductDto>

    // ── Recipes ──────────────────────────────────────
    @GET("api/recipes")
    suspend fun searchRecipes(
        @Query("q") query: String = "",
        @Query("tag") tag: String? = null
    ): List<RecipeResponse>

    @POST("api/recipes")
    suspend fun createRecipe(@Body req: RecipeRequest): RecipeResponse

    // ── Workouts ─────────────────────────────────────
    @GET("api/workouts")
    suspend fun getWorkouts(@Query("date") date: String): List<WorkoutResponse>

    @POST("api/workouts")
    suspend fun logWorkout(@Body req: WorkoutRequest): WorkoutResponse

    // ── Notifications (lokalne — backend trzyma tylko preferencje) ──
    @PUT("api/notifications/settings")
    suspend fun updateNotificationSettings(@Body req: NotificationSettingsRequest)
}
