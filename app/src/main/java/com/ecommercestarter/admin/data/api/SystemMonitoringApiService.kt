package com.ecommercestarter.admin.data.api

import com.ecommercestarter.admin.data.model.AcknowledgeErrorResponse
import com.ecommercestarter.admin.data.model.PerformanceMetricsResponse
import com.ecommercestarter.admin.data.model.ServiceErrorsResponse
import com.ecommercestarter.admin.data.model.ServiceStatusResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SystemMonitoringApiService {
    
    @GET("api/admin/service/status")
    suspend fun getServiceStatus(): Response<ServiceStatusResponse>
    
    @GET("api/admin/service/errors")
    suspend fun getServiceErrors(
        @Query("severity") severity: String? = null,
        @Query("source") source: String? = null,
        @Query("acknowledged") acknowledged: Boolean? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ServiceErrorsResponse>
    
    @GET("api/admin/service/metrics")
    suspend fun getPerformanceMetrics(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("groupBy") groupBy: String = "hour"
    ): Response<PerformanceMetricsResponse>
    
    @PUT("api/admin/service/errors/{id}/acknowledge")
    suspend fun acknowledgeError(@Path("id") errorId: Int): Response<AcknowledgeErrorResponse>
}
