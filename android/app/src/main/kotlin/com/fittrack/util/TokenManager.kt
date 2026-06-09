package com.fittrack.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("fittrack_tokens")

/**
 * Trzyma access/refresh JWT w DataStore. AuthInterceptor czyta accessToken
 * synchronicznie (runBlocking) — to OK, bo jest na OkHttp thread.
 */
@Singleton
class TokenManager @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val ctx: Context
) {
    private val ACCESS  = stringPreferencesKey("access_token")
    private val REFRESH = stringPreferencesKey("refresh_token")

    fun accessTokenBlocking(): String? = runBlocking {
        ctx.dataStore.data.map { it[ACCESS] }.first()
    }

    suspend fun save(access: String, refresh: String) {
        ctx.dataStore.edit {
            it[ACCESS]  = access
            it[REFRESH] = refresh
        }
    }

    suspend fun clear() {
        ctx.dataStore.edit { it.clear() }
    }

    fun isLoggedIn(): Boolean = accessTokenBlocking()?.isNotBlank() == true
}
