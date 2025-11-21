package com.ecommercestarter.admin.data.repository

import android.util.Log
import com.ecommercestarter.admin.data.api.SecurityApiService
import com.ecommercestarter.admin.data.model.SecuritySettings
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for security settings data operations
 */
@Singleton
class SecurityRepository @Inject constructor(
    private val apiService: SecurityApiService
) {
    
    companion object {
        private const val TAG = "SecurityRepository"
    }
    
    /**
     * Get security settings
     */
    suspend fun getSecuritySettings(): Result<SecuritySettings> {
        return try {
            val response = apiService.getSecuritySettings()
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Log.d(TAG, "Security settings retrieved successfully")
                    Result.success(body.data)
                } else {
                    val error = "Failed to retrieve security settings"
                    Log.e(TAG, error)
                    Result.failure(Exception(error))
                }
            } else {
                val error = "API call failed: ${response.code()} - ${response.message()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception retrieving security settings", e)
            Result.failure(e)
        }
    }
}
