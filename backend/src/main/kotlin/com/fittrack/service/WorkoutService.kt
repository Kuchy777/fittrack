package com.fittrack.service

import com.fittrack.dto.*
import com.fittrack.entity.*
import com.fittrack.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class WorkoutService(
    private val userRepo: UserRepository,
    private val workoutRepo: WorkoutActivityRepository
) {
    @Transactional
    fun log(email: String, req: WorkoutRequest): WorkoutResponse {
        val user = userRepo.findByEmail(email).orElseThrow()
        val w = workoutRepo.save(WorkoutActivity(
            user         = user,
            activityDate = req.activityDate,
            activityType = req.activityType,
            durationMin  = req.durationMin,
            kcalBurned   = req.kcalBurned,
            distanceKm   = req.distanceKm,
            avgHeartRate = req.avgHeartRate,
            notes        = req.notes
        ))
        return w.toResponse()
    }

    fun getForDate(email: String, date: LocalDate): List<WorkoutResponse> {
        val user = userRepo.findByEmail(email).orElseThrow()
        return workoutRepo.findAllByUserIdAndActivityDate(user.id, date).map { it.toResponse() }
    }

    private fun WorkoutActivity.toResponse() = WorkoutResponse(
        id           = id,
        activityDate = activityDate,
        activityType = activityType,
        durationMin  = durationMin,
        kcalBurned   = kcalBurned,
        distanceKm   = distanceKm,
        notes        = notes
    )
}
