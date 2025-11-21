package com.ecommercestarter.admin.data.repository

import android.util.Log
import com.ecommercestarter.admin.data.api.AuthApiService
import com.ecommercestarter.admin.data.api.LogoutRequest
import com.ecommercestarter.admin.data.api.RefreshTokenRequest
import com.ecommercestarter.admin.data.model.LoginRequest
import com.ecommercestarter.admin.data.model.UserDto
import com.ecommercestarter.admin.data.preferences.UserPreferences
import com.ecommercestarter.admin.domain.model.User
import com.ecommercestarter.admin.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val userPreferences: UserPreferences
) : AuthRepository {
    
    companion object {
        private const val TAG = "AuthRepository"
    }
    
    override suspend fun login(
        email: String,
        password: String,
        rememberMe: Boolean
    ): Result<User> {
        return try {
            Log.d(TAG, "=== LOGIN ATTEMPT ===")
            Log.d(TAG, "Email: $email")
            
            val request = LoginRequest(email, password, rememberMe)
            val response = authApiService.login(request)
            
            Log.d(TAG, "Response code: ${response.code()}")
            Log.d(TAG, "Response successful: ${response.isSuccessful}")
            Log.d(TAG, "Raw response body: ${response.raw()}")
            
            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                
                Log.d(TAG, "Response body success: ${body.success}")
                Log.d(TAG, "Access token present: ${body.token != null}")
                Log.d(TAG, "Refresh token present: ${body.refreshToken != null}")
                Log.d(TAG, "User data present: ${body.user != null}")
                
                if (body.token != null) {
                    Log.d(TAG, "=== FULL ACCESS TOKEN ===")
                    Log.d(TAG, body.token)
                    Log.d(TAG, "=== END ACCESS TOKEN (length: ${body.token.length}) ===")
                }
                if (body.refreshToken != null) {
                    Log.d(TAG, "=== FULL REFRESH TOKEN ===")
                    Log.d(TAG, body.refreshToken)
                    Log.d(TAG, "=== END REFRESH TOKEN (length: ${body.refreshToken.length}) ===")
                } else {
                    Log.e(TAG, "!!! REFRESH TOKEN IS NULL !!!")
                }
                
                val accessToken = body.token ?: return Result.failure(Exception("No access token received"))
                val refreshToken = body.refreshToken ?: return Result.failure(Exception("No refresh token received"))
                val userDto = body.user ?: return Result.failure(Exception("No user data received"))
                
                // Save both tokens
                Log.d(TAG, "Saving tokens to preferences...")
                userPreferences.saveTokens(accessToken, refreshToken)
                userPreferences.saveUserData(userDto)
                
                Log.d(TAG, "Login successful!")
                Result.success(userDto.toDomainModel())
            } else {
                val errorMsg = response.body()?.message ?: "Login failed"
                Log.e(TAG, "Login failed: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login exception: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            val refreshToken = userPreferences.refreshToken.first()
            if (refreshToken != null) {
                authApiService.logout(LogoutRequest(refreshToken))
            }
            userPreferences.clearAll()
            Result.success(Unit)
        } catch (e: Exception) {
            userPreferences.clearAll()
            Result.failure(e)
        }
    }
    
    override suspend fun refreshAccessToken(): Result<Unit> {
        return try {
            val accessToken = userPreferences.authToken.first()
            val refreshToken = userPreferences.refreshToken.first()
            
            if (accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) {
                return Result.failure(Exception("No tokens available"))
            }
            
            val request = RefreshTokenRequest(accessToken, refreshToken)
            val response = authApiService.refreshToken(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                val newAccessToken = body.token ?: return Result.failure(Exception("No access token received"))
                val newRefreshToken = body.refreshToken ?: return Result.failure(Exception("No refresh token received"))
                
                // Save new tokens
                userPreferences.saveTokens(newAccessToken, newRefreshToken)
                
                Result.success(Unit)
            } else {
                // Refresh failed - tokens expired, need to log in again
                userPreferences.clearAll()
                Result.failure(Exception("Token refresh failed"))
            }
        } catch (e: Exception) {
            userPreferences.clearAll()
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentUser(): Flow<User?> {
        return userPreferences.userData.map { it?.toDomainModel() }
    }
    
    override suspend fun isAuthenticated(): Flow<Boolean> {
        return userPreferences.authToken.map { !it.isNullOrEmpty() }
    }
    
    private fun UserDto.toDomainModel(): User {
        return User(
            id = id,
            email = email,
            name = name,
            role = role,
            avatarUrl = avatarUrl
        )
    }
}
