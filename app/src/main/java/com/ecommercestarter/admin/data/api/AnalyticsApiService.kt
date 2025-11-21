package com.ecommercestarter.admin.data.api

import com.ecommercestarter.admin.data.model.AnalyticsSummaryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service for analytics
 */
interface AnalyticsApiService {
    
    /**
     * Get analytics summary for a date range
     */
    @GET("api/analytics/summary")
    suspend fun getAnalyticsSummary(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<AnalyticsSummaryResponse>
}
