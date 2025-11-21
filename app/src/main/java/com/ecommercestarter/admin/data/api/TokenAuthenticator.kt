package com.ecommercestarter.admin.data.api

import com.ecommercestarter.admin.data.preferences.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val authApiServiceProvider: Provider<AuthApiService>,
    private val userPreferences: UserPreferences
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Don't retry if we already tried to refresh (avoid infinite loop)
        if (response.request.header("X-Retry-Count") != null) {
            return null
        }

        // Get tokens
        val refreshToken = runBlocking {
            userPreferences.refreshToken.first()
        }
        val accessToken = runBlocking {
            userPreferences.authToken.first()
        }

        if (refreshToken.isNullOrEmpty() || accessToken.isNullOrEmpty()) {
            // No refresh token available, logout
            runBlocking {
                userPreferences.clearAuthData()
            }
            return null
        }

        // Try to refresh the access token
        return try {
            val refreshResponse = runBlocking {
                authApiServiceProvider.get().refreshToken(
                    RefreshTokenRequest(
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )
                )
            }

            if (refreshResponse.isSuccessful) {
                val newTokens = refreshResponse.body()
                if (newTokens != null && newTokens.success) {
                    // Save new tokens
                    runBlocking {
                        userPreferences.saveAuthToken(newTokens.token ?: "")
                        newTokens.refreshToken?.let { 
                            userPreferences.saveRefreshToken(it)
                        }
                    }

                    // Retry the original request with new token
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.token}")
                        .header("X-Retry-Count", "1")
                        .build()
                } else {
                    // Refresh failed, logout
                    runBlocking {
                        userPreferences.clearAuthData()
                    }
                    null
                }
            } else {
                // Refresh failed, logout
                runBlocking {
                    userPreferences.clearAuthData()
                }
                null
            }
        } catch (e: Exception) {
            // Refresh failed, logout
            runBlocking {
                userPreferences.clearAuthData()
            }
            null
        }
    }
}
