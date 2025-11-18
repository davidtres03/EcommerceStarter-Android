package com.ecommercestarter.admin.domain.repository

import com.ecommercestarter.admin.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String, rememberMe: Boolean): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Flow<User?>
    suspend fun isAuthenticated(): Flow<Boolean>
}
