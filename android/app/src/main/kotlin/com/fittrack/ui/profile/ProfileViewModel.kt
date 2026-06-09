package com.fittrack.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.model.*
import com.fittrack.data.repository.ProfileRepository
import com.fittrack.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: ProfileRepository
) : ViewModel() {

    private val _profile = MutableStateFlow<Resource<ProfileResponse>?>(null)
    val profile: StateFlow<Resource<ProfileResponse>?> = _profile

    init { loadProfile() }

    fun loadProfile() = viewModelScope.launch {
        _profile.value = Resource.Loading
        _profile.value = repo.getProfile()
    }

    fun updateProfile(req: ProfileUpdateRequest) = viewModelScope.launch {
        _profile.value = Resource.Loading
        _profile.value = repo.update(req)
    }
}
