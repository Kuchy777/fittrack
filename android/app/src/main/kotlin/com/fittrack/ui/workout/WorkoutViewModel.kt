package com.fittrack.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.model.*
import com.fittrack.data.repository.WorkoutRepository
import com.fittrack.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repo: WorkoutRepository
) : ViewModel() {

    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    private val _workouts = MutableStateFlow<Resource<List<WorkoutResponse>>>(Resource.Loading)
    val workouts: StateFlow<Resource<List<WorkoutResponse>>> = _workouts

    private val _logState = MutableStateFlow<Resource<WorkoutResponse>?>(null)
    val logState: StateFlow<Resource<WorkoutResponse>?> = _logState

    fun loadToday() = load(LocalDate.now())

    fun load(date: LocalDate) = viewModelScope.launch {
        _workouts.value = Resource.Loading
        _workouts.value = repo.getForDate(date.format(fmt))
    }

    fun logWorkout(req: WorkoutRequest) = viewModelScope.launch {
        _logState.value = Resource.Loading
        _logState.value = repo.log(req)
        loadToday()
    }

    fun addGpsPoints(workoutId: Long, points: List<GpsPointRequest>) = viewModelScope.launch {
        repo.addGpsPoints(workoutId, points)
    }
}
