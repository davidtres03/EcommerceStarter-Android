package com.ecommercestarter.admin.data.repository

import com.ecommercestarter.admin.data.api.AccountApiService
import com.ecommercestarter.admin.data.api.AccountUpdateResponse
import com.ecommercestarter.admin.data.api.ChangePasswordRequest
import com.ecommercestarter.admin.data.api.UpdateEmailRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val accountApiService: AccountApiService
) {
    
    suspend fun updateEmail(newEmail: String, currentPassword: String): Response<AccountUpdateResponse> {
        return accountApiService.updateEmail(
            UpdateEmailRequest(newEmail, currentPassword)
        )
    }
    
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Response<AccountUpdateResponse> {
        return accountApiService.changePassword(
            ChangePasswordRequest(currentPassword, newPassword, confirmPassword)
        )
    }
}
