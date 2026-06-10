package com.fittrack.data.repository

import com.fittrack.data.api.FitTrackApi
import com.fittrack.data.model.*
import com.fittrack.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepository @Inject constructor(private val api: FitTrackApi) {

    suspend fun getEntries(date: String): Resource<List<DiaryEntryResponse>> = try {
        Resource.Success(api.getDiaryEntries(date))
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Blad pobierania wpisow")
    }

    suspend fun addEntry(req: DiaryEntryRequest): Resource<DiaryEntryResponse> = try {
        Resource.Success(api.addDiaryEntry(req))
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Blad dodawania wpisu")
    }

    suspend fun deleteEntry(id: Long): Resource<Unit> = try {
        api.deleteDiaryEntry(id); Resource.Success(Unit)
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Blad usuwania")
    }

    suspend fun getDailySummary(date: String): Resource<DailySummaryResponse> = try {
        Resource.Success(api.getDailySummary(date))
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Blad podsumowania")
    }

    suspend fun searchFood(query: String): Resource<List<FoodProductDto>> = try {
        Resource.Success(api.searchFood(query))
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Blad wyszukiwania")
    }
}
