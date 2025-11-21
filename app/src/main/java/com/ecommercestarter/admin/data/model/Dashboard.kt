package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("timestamp")
    val timestamp: String,
    
    @SerializedName("metrics")
    val metrics: DashboardMetrics
)

data class DashboardMetrics(
    @SerializedName("pendingOrders")
    val pendingOrders: Int,
    
    @SerializedName("todayRevenue")
    val todayRevenue: Double,
    
    @SerializedName("todayOrders")
    val todayOrders: Int,
    
    @SerializedName("websiteStatus")
    val websiteStatus: String,
    
    @SerializedName("lastStatusCheck")
    val lastStatusCheck: String,
    
    @SerializedName("traffic")
    val traffic: TrafficData,
    
    @SerializedName("inventoryAlerts")
    val inventoryAlerts: Int,
    
    @SerializedName("supportQueueLength")
    val supportQueueLength: Int,
    
    @SerializedName("uptime")
    val uptime: Double,
    
    @SerializedName("lastBackupTime")
    val lastBackupTime: String?
)

data class TrafficData(
    @SerializedName("last24Hours")
    val last24Hours: List<Int>,
    
    @SerializedName("peak")
    val peak: Int,
    
    @SerializedName("average")
    val average: Int
)
