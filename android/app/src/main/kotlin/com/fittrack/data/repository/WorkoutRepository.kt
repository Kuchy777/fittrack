package com.fittrack.data.repository

import com.fittrack.data.api.FitTrackApi
import com.fittrack.data.model.GpsPointRequest
import com.fittrack.data.model.WorkoutRequest
import com.fittrack.data.model.WorkoutResponse
import com.fittrack.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(private val api: FitTrackApi) {

    suspend fun getForDate(date: String): Resource<List<WorkoutResponse>> = try {
        Resource.Success(api.getWorkouts(date))
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Błąd pobierania treningów")
    }

    suspend fun log(req: WorkoutRequest): Resource<WorkoutResponse> = try {
        Resource.Success(api.logWorkout(req))
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Błąd zapisu treningu")
    }

    suspend fun addGpsPoints(workoutId: Long, points: List<GpsPointRequest>): Resource<Unit> = try {
        api.addGpsPoints(workoutId, points); Resource.Success(Unit)
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Błąd wysyłki GPS")
    }
}
