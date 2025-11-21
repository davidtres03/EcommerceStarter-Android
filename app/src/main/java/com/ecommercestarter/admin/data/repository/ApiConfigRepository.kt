package com.ecommercestarter.admin.data.repository

import android.util.Log
import com.ecommercestarter.admin.data.api.ApiConfigApiService
import com.ecommercestarter.admin.data.model.ApiConfigGroup
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for API configuration data operations
 */
@Singleton
class ApiConfigRepository @Inject constructor(
    private val apiService: ApiConfigApiService
) {
    
    companion object {
        private const val TAG = "ApiConfigRepository"
    }
    
    /**
     * Get all API configurations grouped by type
     */
    suspend fun getApiConfigurations(): Result<List<ApiConfigGroup>> {
        return try {
            val response = apiService.getApiConfigurations()
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Log.d(TAG, "API configurations retrieved successfully: ${body.totalCount} total")
                    Result.success(body.data)
                } else {
                    val error = "Failed to retrieve API configurations"
                    Log.e(TAG, error)
                    Result.failure(Exception(error))
                }
            } else {
                val error = "API call failed: ${response.code()} - ${response.message()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception retrieving API configurations", e)
            Result.failure(e)
        }
    }
    
    /**
     * Toggle an API configuration active/inactive
     */
    suspend fun toggleApiConfiguration(configId: Int): Result<Boolean> {
        return try {
            val response = apiService.toggleApiConfiguration(configId)
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    val newState = body.data?.isActive ?: false
                    Log.d(TAG, "API configuration $configId toggled successfully: ${if (newState) "Active" else "Inactive"}")
                    Result.success(newState)
                } else {
                    val error = body.message
                    Log.e(TAG, "Toggle failed: $error")
                    Result.failure(Exception(error))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val error = "Toggle failed: ${response.code()} - $errorBody"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception toggling API configuration $configId", e)
            Result.failure(e)
        }
    }
}
