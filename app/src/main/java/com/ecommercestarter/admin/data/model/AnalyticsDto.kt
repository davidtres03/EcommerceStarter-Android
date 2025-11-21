package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response wrapper for analytics summary
 */
data class AnalyticsSummaryResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("data")
    val data: AnalyticsData
)

/**
 * Analytics data container
 */
data class AnalyticsData(
    @SerializedName("dateRange")
    val dateRange: DateRange,
    
    @SerializedName("summary")
    val summary: AnalyticsSummary,
    
    @SerializedName("dailyBreakdown")
    val dailyBreakdown: List<DailyStats>
)

/**
 * Date range for analytics
 */
data class DateRange(
    @SerializedName("start")
    val start: String,
    
    @SerializedName("end")
    val end: String
)

data class AnalyticsSummary(
    @SerializedName("totalSessions")
    val totalSessions: Int,
    
    @SerializedName("totalPageViews")
    val totalPageViews: Int,
    
    @SerializedName("totalEvents")
    val totalEvents: Int,
    
    @SerializedName("uniqueVisitors")
    val uniqueVisitors: Int,
    
    @SerializedName("conversions")
    val conversions: Int,
    
    @SerializedName("conversionRate")
    val conversionRate: Double,
    
    @SerializedName("avgSessionDuration")
    val avgSessionDuration: Double,
    
    @SerializedName("avgPagesPerSession")
    val avgPagesPerSession: Double,
    
    @SerializedName("bounceRate")
    val bounceRate: Double
)

data class DailyStats(
    @SerializedName("date")
    val date: String,
    
    @SerializedName("sessions")
    val sessions: Int,
    
    @SerializedName("pageViews")
    val pageViews: Int,
    
    @SerializedName("uniqueVisitors")
    val uniqueVisitors: Int,
    
    @SerializedName("conversions")
    val conversions: Int
)
