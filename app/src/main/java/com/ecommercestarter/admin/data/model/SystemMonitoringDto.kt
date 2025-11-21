package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

data class ServiceStatusResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: ServiceStatus
)

data class ServiceStatus(
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("isWebServiceOnline")
    val isWebServiceOnline: Boolean,
    @SerializedName("responseTimeMs")
    val responseTimeMs: Int,
    @SerializedName("isBackgroundServiceRunning")
    val isBackgroundServiceRunning: Boolean,
    @SerializedName("pendingOrdersCount")
    val pendingOrdersCount: Int,
    @SerializedName("memoryUsageMb")
    val memoryUsageMb: Double,
    @SerializedName("cpuUsagePercent")
    val cpuUsagePercent: Double,
    @SerializedName("databaseConnected")
    val databaseConnected: Boolean,
    @SerializedName("activeUserCount")
    val activeUserCount: Int,
    @SerializedName("queueSize")
    val queueSize: Int,
    @SerializedName("uptimePercent")
    val uptimePercent: Double,
    @SerializedName("errorMessage")
    val errorMessage: String?,
    @SerializedName("recentUpdates")
    val recentUpdates: List<RecentUpdate>,
    @SerializedName("recentErrors")
    val recentErrors: List<ServiceError>
)

data class RecentUpdate(
    @SerializedName("version")
    val version: String,
    @SerializedName("appliedAt")
    val appliedAt: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("releaseNotes")
    val releaseNotes: String?,
    @SerializedName("applyDurationSeconds")
    val applyDurationSeconds: Int?
)

data class ServiceError(
    @SerializedName("id")
    val id: Int,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("severity")
    val severity: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("isAcknowledged")
    val isAcknowledged: Boolean
)

data class ServiceErrorsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: ErrorsData
)

data class ErrorsData(
    @SerializedName("errors")
    val errors: List<ServiceErrorDetail>,
    @SerializedName("totalCount")
    val totalCount: Int,
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("totalPages")
    val totalPages: Int
)

data class ServiceErrorDetail(
    @SerializedName("id")
    val id: Int,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("severity")
    val severity: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("stackTrace")
    val stackTrace: String?,
    @SerializedName("isAcknowledged")
    val isAcknowledged: Boolean,
    @SerializedName("acknowledgedAt")
    val acknowledgedAt: String?
)

data class PerformanceMetricsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: PerformanceMetrics
)

data class PerformanceMetrics(
    @SerializedName("metrics")
    val metrics: List<PerformanceMetric>,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("averageResponseTime")
    val averageResponseTime: Double,
    @SerializedName("averageMemoryUsage")
    val averageMemoryUsage: Double,
    @SerializedName("averageCpuUsage")
    val averageCpuUsage: Double,
    @SerializedName("averageUptime")
    val averageUptime: Double
)

data class PerformanceMetric(
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("responseTimeMs")
    val responseTimeMs: Int,
    @SerializedName("memoryUsageMb")
    val memoryUsageMb: Double,
    @SerializedName("cpuUsagePercent")
    val cpuUsagePercent: Double,
    @SerializedName("uptimePercent")
    val uptimePercent: Double,
    @SerializedName("activeUserCount")
    val activeUserCount: Int
)

data class AcknowledgeErrorResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)
