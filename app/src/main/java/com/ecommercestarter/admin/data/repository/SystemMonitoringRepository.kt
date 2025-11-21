package com.ecommercestarter.admin.data.repository

import android.util.Log
import com.ecommercestarter.admin.data.api.SystemMonitoringApiService
import com.ecommercestarter.admin.data.model.PerformanceMetrics
import com.ecommercestarter.admin.data.model.ServiceErrorDetail
import com.ecommercestarter.admin.data.model.ServiceStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemMonitoringRepository @Inject constructor(
    private val apiService: SystemMonitoringApiService
) {
    suspend fun getServiceStatus(): Result<ServiceStatus> {
        return try {
            Log.d("SystemMonitoringRepo", "Fetching service status...")
            val response = apiService.getServiceStatus()
            
            if (response.isSuccessful && response.body()?.success == true) {
                val status = response.body()!!.data
                Log.d("SystemMonitoringRepo", "Successfully loaded service status")
                Result.success(status)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to load service status"
                Log.e("SystemMonitoringRepo", "Error loading service status: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("SystemMonitoringRepo", "Exception loading service status: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getServiceErrors(
        severity: String? = null,
        source: String? = null,
        acknowledged: Boolean? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<Pair<List<ServiceErrorDetail>, Int>> {
        return try {
            Log.d("SystemMonitoringRepo", "Fetching service errors (page $page)...")
            val response = apiService.getServiceErrors(severity, source, acknowledged, page, pageSize)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data
                Log.d("SystemMonitoringRepo", "Successfully loaded ${data.errors.size} errors")
                Result.success(Pair(data.errors, data.totalCount))
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to load service errors"
                Log.e("SystemMonitoringRepo", "Error loading service errors: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("SystemMonitoringRepo", "Exception loading service errors: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getPerformanceMetrics(
        startDate: String? = null,
        endDate: String? = null,
        groupBy: String = "hour"
    ): Result<PerformanceMetrics> {
        return try {
            Log.d("SystemMonitoringRepo", "Fetching performance metrics...")
            val response = apiService.getPerformanceMetrics(startDate, endDate, groupBy)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val metrics = response.body()!!.data
                Log.d("SystemMonitoringRepo", "Successfully loaded ${metrics.metrics.size} metrics")
                Result.success(metrics)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to load performance metrics"
                Log.e("SystemMonitoringRepo", "Error loading performance metrics: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("SystemMonitoringRepo", "Exception loading performance metrics: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun acknowledgeError(errorId: Int): Result<String> {
        return try {
            Log.d("SystemMonitoringRepo", "Acknowledging error $errorId...")
            val response = apiService.acknowledgeError(errorId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val message = response.body()!!.message
                Log.d("SystemMonitoringRepo", "Successfully acknowledged error")
                Result.success(message)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to acknowledge error"
                Log.e("SystemMonitoringRepo", "Error acknowledging error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("SystemMonitoringRepo", "Exception acknowledging error: ${e.message}", e)
            Result.failure(e)
        }
    }
}
