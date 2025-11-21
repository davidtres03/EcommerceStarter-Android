package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response wrapper for security settings
 */
data class SecuritySettingsResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("data")
    val data: SecuritySettings
)

/**
 * Complete security settings data
 */
data class SecuritySettings(
    @SerializedName("rateLimiting")
    val rateLimiting: RateLimitingSettings,
    
    @SerializedName("ipBlocking")
    val ipBlocking: IpBlockingSettings,
    
    @SerializedName("accountLockout")
    val accountLockout: AccountLockoutSettings,
    
    @SerializedName("auditLogging")
    val auditLogging: AuditLoggingSettings,
    
    @SerializedName("notifications")
    val notifications: NotificationSettings,
    
    @SerializedName("advanced")
    val advanced: AdvancedSettings,
    
    @SerializedName("lastModified")
    val lastModified: String?,
    
    @SerializedName("lastModifiedBy")
    val lastModifiedBy: String?
)

data class RateLimitingSettings(
    @SerializedName("enabled")
    val enabled: Boolean,
    
    @SerializedName("maxRequestsPerMinute")
    val maxRequestsPerMinute: Int,
    
    @SerializedName("maxRequestsPerSecond")
    val maxRequestsPerSecond: Int,
    
    @SerializedName("maxRequestsPerMinuteAuth")
    val maxRequestsPerMinuteAuth: Int,
    
    @SerializedName("maxRequestsPerSecondAuth")
    val maxRequestsPerSecondAuth: Int,
    
    @SerializedName("exemptAdmins")
    val exemptAdmins: Boolean
)

data class IpBlockingSettings(
    @SerializedName("enabled")
    val enabled: Boolean,
    
    @SerializedName("maxFailedLoginAttempts")
    val maxFailedLoginAttempts: Int,
    
    @SerializedName("failedLoginWindowMinutes")
    val failedLoginWindowMinutes: Int,
    
    @SerializedName("blockDurationMinutes")
    val blockDurationMinutes: Int
)

data class AccountLockoutSettings(
    @SerializedName("enabled")
    val enabled: Boolean,
    
    @SerializedName("maxAttempts")
    val maxAttempts: Int,
    
    @SerializedName("durationMinutes")
    val durationMinutes: Int
)

data class AuditLoggingSettings(
    @SerializedName("enabled")
    val enabled: Boolean,
    
    @SerializedName("retentionDays")
    val retentionDays: Int
)

data class NotificationSettings(
    @SerializedName("notifyOnCriticalEvents")
    val notifyOnCriticalEvents: Boolean,
    
    @SerializedName("notifyOnIpBlocking")
    val notifyOnIpBlocking: Boolean,
    
    @SerializedName("notificationEmail")
    val notificationEmail: String?
)

data class AdvancedSettings(
    @SerializedName("enableGeoIpBlocking")
    val enableGeoIpBlocking: Boolean,
    
    @SerializedName("blockedCountries")
    val blockedCountries: String?,
    
    @SerializedName("whitelistedIps")
    val whitelistedIps: String?,
    
    @SerializedName("blacklistedIps")
    val blacklistedIps: String?
)
