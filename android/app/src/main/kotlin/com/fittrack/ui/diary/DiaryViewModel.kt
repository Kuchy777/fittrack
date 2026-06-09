package com.fittrack.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.model.*
import com.fittrack.data.repository.DiaryRepository
import com.fittrack.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repo: DiaryRepository
) : ViewModel() {

    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    private val _entries = MutableStateFlow<Resource<List<DiaryEntryResponse>>>(Resource.Loading)
    val entries: StateFlow<Resource<List<DiaryEntryResponse>>> = _entries

    private val _summary = MutableStateFlow<Resource<DailySummaryResponse>?>(null)
    val summary: StateFlow<Resource<DailySummaryResponse>?> = _summary

    private val _foodSearch = MutableStateFlow<Resource<List<FoodProductDto>>?>(null)
    val foodSearch: StateFlow<Resource<List<FoodProductDto>>?> = _foodSearch

    private val _deleteState = MutableStateFlow<Resource<Unit>?>(null)
    val deleteState: StateFlow<Resource<Unit>?> = _deleteState

    var currentDate: LocalDate = LocalDate.now()
        private set

    fun loadDay(date: LocalDate = LocalDate.now()) {
        currentDate = date
        viewModelScope.launch {
            _entries.value = Resource.Loading
            _entries.value = repo.getEntries(date.format(fmt))
            _summary.value = repo.getDailySummary(date.format(fmt))
        }
    }

    fun addEntry(req: DiaryEntryRequest) = viewModelScope.launch {
        repo.addEntry(req)
        loadDay(currentDate)
    }

    fun deleteEntry(id: Long) = viewModelScope.launch {
        _deleteState.value = repo.deleteEntry(id)
        loadDay(currentDate)
    }

    fun searchFood(query: String) = viewModelScope.launch {
        if (query.length >= 3) {
            _foodSearch.value = Resource.Loading
            _foodSearch.value = repo.searchFood(query)
        }
    }

    fun getFoodByBarcode(code: String) = viewModelScope.launch {
        _foodSearch.value = Resource.Loading
        _foodSearch.value = when (val res = repo.getFoodByBarcode(code)) {
            is Resource.Success -> Resource.Success(listOf(res.data))
            is Resource.Error   -> Resource.Error(res.message)
            else -> Resource.Loading
        }
    }
}
