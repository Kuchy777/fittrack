package com.fittrack.data.repository

import com.fittrack.data.api.FitTrackApi
import com.fittrack.data.model.ProfileResponse
import com.fittrack.data.model.ProfileUpdateRequest
import com.fittrack.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(private val api: FitTrackApi) {

    suspend fun getProfile(): Resource<ProfileResponse> = try {
        Resource.Success(api.getProfile())
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Błąd pobierania profilu")
    }

    suspend fun update(req: ProfileUpdateRequest): Resource<ProfileResponse> = try {
        Resource.Success(api.updateProfile(req))
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Błąd aktualizacji profilu")
    }
}
