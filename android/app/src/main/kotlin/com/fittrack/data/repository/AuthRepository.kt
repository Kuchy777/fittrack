package com.fittrack.data.repository

import com.fittrack.data.api.FitTrackApi
import com.fittrack.data.model.LoginRequest
import com.fittrack.data.model.RegisterRequest
import com.fittrack.util.Resource
import com.fittrack.util.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: FitTrackApi,
    private val tokens: TokenManager
) {
    suspend fun register(email: String, password: String): Resource<Unit> = try {
        val resp = api.register(RegisterRequest(email, password))
        tokens.save(resp.accessToken, resp.refreshToken)
        Resource.Success(Unit)
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Rejestracja nieudana")
    }

    suspend fun login(email: String, password: String): Resource<Unit> = try {
        val resp = api.login(LoginRequest(email, password))
        tokens.save(resp.accessToken, resp.refreshToken)
        Resource.Success(Unit)
    } catch (t: Throwable) {
        Resource.Error(t.localizedMessage ?: "Logowanie nieudane")
    }

    suspend fun logout() = tokens.clear()

    fun isLoggedIn(): Boolean = tokens.isLoggedIn()
}
