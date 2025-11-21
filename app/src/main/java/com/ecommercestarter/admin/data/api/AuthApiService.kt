package com.ecommercestarter.admin.data.api

import com.ecommercestarter.admin.data.model.LoginRequest
import com.ecommercestarter.admin.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
    
    @POST("api/auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<LoginResponse>
    
    @POST("api/auth/logout")
    suspend fun logout(
        @Body request: LogoutRequest
    ): Response<Unit>
    
    @GET("api/auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<LoginResponse>
}

data class RefreshTokenRequest(
    val accessToken: String,
    val refreshToken: String
)

data class LogoutRequest(
    val refreshToken: String
)
