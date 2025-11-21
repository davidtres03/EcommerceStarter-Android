package com.ecommercestarter.admin.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Query

interface AccountApiService {
    
    @PUT("api/account/email")
    suspend fun updateEmail(
        @Body request: UpdateEmailRequest
    ): Response<AccountUpdateResponse>
    
    @PUT("api/account/password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<AccountUpdateResponse>
}

data class UpdateEmailRequest(
    val newEmail: String,
    val currentPassword: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

data class AccountUpdateResponse(
    val success: Boolean,
    val message: String
)
