package com.fittrack.data.api

import com.fittrack.util.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        // Endpointy /api/auth/* nie wymagają tokenu
        val path = req.url.encodedPath
        val skip = path.startsWith("/api/auth/")
        val builder = req.newBuilder()
        if (!skip) {
            tokenManager.accessTokenBlocking()?.let { token ->
                builder.header("Authorization", "Bearer $token")
            }
        }
        return chain.proceed(builder.build())
    }
}
