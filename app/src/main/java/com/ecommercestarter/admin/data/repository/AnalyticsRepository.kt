package com.ecommercestarter.admin.data.repository

import android.util.Log
import com.ecommercestarter.admin.data.api.AnalyticsApiService
import com.ecommercestarter.admin.data.model.AnalyticsData
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for analytics data operations
 */
@Singleton
class AnalyticsRepository @Inject constructor(
    private val apiService: AnalyticsApiService
) {
    
    companion object {
        private const val TAG = "AnalyticsRepository"
    }
    
    /**
     * Get analytics summary for date range (defaults to last 30 days)
     */
    suspend fun getAnalyticsSummary(
        startDate: String? = null,
        endDate: String? = null
    ): Result<AnalyticsData> {
        return try {
            val response = apiService.getAnalyticsSummary(startDate, endDate)
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Log.d(TAG, "Analytics summary retrieved: ${body.data.summary.totalSessions} sessions")
                    Result.success(body.data)
                } else {
                    val error = "Failed to retrieve analytics summary"
                    Log.e(TAG, error)
                    Result.failure(Exception(error))
                }
            } else {
                val error = "API call failed: ${response.code()} - ${response.message()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception retrieving analytics summary", e)
            Result.failure(e)
        }
    }
}
