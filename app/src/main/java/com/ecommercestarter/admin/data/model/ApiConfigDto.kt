package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response wrapper for API configurations grouped by type
 */
data class ApiConfigurationsResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("data")
    val data: List<ApiConfigGroup>,
    
    @SerializedName("totalCount")
    val totalCount: Int
)

/**
 * Group of API configurations by type (Email, Payment, Analytics, etc.)
 */
data class ApiConfigGroup(
    @SerializedName("apiType")
    val apiType: String,
    
    @SerializedName("configurations")
    val configurations: List<ApiConfiguration>,
    
    @SerializedName("count")
    val count: Int
)

/**
 * Individual API configuration details
 */
data class ApiConfiguration(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("apiType")
    val apiType: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("isActive")
    val isActive: Boolean,
    
    @SerializedName("isTestMode")
    val isTestMode: Boolean,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("hasCredentials")
    val hasCredentials: Boolean,
    
    @SerializedName("lastValidated")
    val lastValidated: String?,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("lastUpdated")
    val lastUpdated: String?,
    
    @SerializedName("createdBy")
    val createdBy: String?,
    
    @SerializedName("updatedBy")
    val updatedBy: String?
)

/**
 * Response for toggle API configuration operation
 */
data class ToggleApiConfigResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: ToggleData?
)

data class ToggleData(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("isActive")
    val isActive: Boolean
)
