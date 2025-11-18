package com.ecommercestarter.admin.data.repository

import com.ecommercestarter.admin.data.api.BrandingApiService
import com.ecommercestarter.admin.data.model.BrandingConfig
import com.ecommercestarter.admin.data.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BrandingRepository @Inject constructor(
    private val brandingApiService: BrandingApiService,
    private val userPreferences: UserPreferences
) {
    
    suspend fun fetchAndCacheBranding(): Result<BrandingConfig> {
        return try {
            val response = brandingApiService.getBranding()
            
            if (response.isSuccessful) {
                val branding = response.body()
                if (branding != null) {
                    // Cache branding data
                    userPreferences.saveBranding(branding)
                    Result.success(branding)
                } else {
                    Result.failure(Exception("No branding data received"))
                }
            } else {
                Result.failure(Exception("Failed to fetch branding: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getCachedBranding(): Flow<BrandingConfig?> {
        return userPreferences.branding
    }
    
    suspend fun clearBranding() {
        userPreferences.clearBranding()
    }
}
