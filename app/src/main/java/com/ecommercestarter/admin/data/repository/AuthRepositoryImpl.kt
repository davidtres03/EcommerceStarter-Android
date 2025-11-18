package com.ecommercestarter.admin.data.repository

import com.ecommercestarter.admin.data.api.AuthApiService
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
    
    override suspend fun login(
        email: String,
        password: String,
        rememberMe: Boolean
    ): Result<User> {
        return try {
            val request = LoginRequest(email, password, rememberMe)
            val response = authApiService.login(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                val token = body.token ?: return Result.failure(Exception("No token received"))
                val userDto = body.user ?: return Result.failure(Exception("No user data received"))
                
                // Save to preferences
                userPreferences.saveAuthToken(token)
                userPreferences.saveUserData(userDto)
                
                Result.success(userDto.toDomainModel())
            } else {
                Result.failure(Exception(response.body()?.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            val token = userPreferences.authToken.first()
            if (token != null) {
                authApiService.logout("Bearer $token")
            }
            userPreferences.clearAll()
            Result.success(Unit)
        } catch (e: Exception) {
            userPreferences.clearAll() // Clear local data anyway
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
