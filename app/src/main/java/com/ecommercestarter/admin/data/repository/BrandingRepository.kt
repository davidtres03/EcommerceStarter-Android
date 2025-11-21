package com.ecommercestarter.admin.data.repository

import android.util.Log
import com.ecommercestarter.admin.data.api.BrandingApiService
import com.ecommercestarter.admin.data.model.BrandingConfig
import com.ecommercestarter.admin.data.model.BrandingSettings
import com.ecommercestarter.admin.data.model.UpdateBrandingRequest
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
            Log.d("BrandingRepository", "Making API call to /api/branding")
            val response = brandingApiService.getBranding()
            
            Log.d("BrandingRepository", "Response code: ${response.code()}")
            if (response.isSuccessful) {
                val branding = response.body()
                if (branding != null) {
                    Log.d("BrandingRepository", "Branding received: primaryColor=${branding.primaryColor}, businessName=${branding.businessName}")
                    // Cache branding data
                    userPreferences.saveBranding(branding)
                    Log.d("BrandingRepository", "Branding saved to preferences")
                    Result.success(branding)
                } else {
                    Log.e("BrandingRepository", "Response body is null")
                    Result.failure(Exception("No branding data received"))
                }
            } else {
                Log.e("BrandingRepository", "Failed response: ${response.code()} - ${response.errorBody()?.string()}")
                Result.failure(Exception("Failed to fetch branding: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("BrandingRepository", "Exception fetching branding", e)
            Result.failure(e)
        }
    }
    
    suspend fun getBrandingSettings(): Result<BrandingSettings> {
        return try {
            Log.d("BrandingRepository", "Fetching detailed branding settings...")
            val response = brandingApiService.getBrandingSettings()
            
            if (response.isSuccessful && response.body()?.success == true) {
                val settings = response.body()!!.data
                Log.d("BrandingRepository", "Successfully loaded branding settings: ${settings.siteName}")
                Result.success(settings)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to load branding settings"
                Log.e("BrandingRepository", "Error loading branding settings: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("BrandingRepository", "Exception loading branding settings: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateBrandingSettings(request: UpdateBrandingRequest): Result<String> {
        return try {
            Log.d("BrandingRepository", "Updating branding settings...")
            val response = brandingApiService.updateBrandingSettings(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val message = response.body()!!.message
                Log.d("BrandingRepository", "Successfully updated branding settings: $message")
                // Refresh cached branding data
                fetchAndCacheBranding()
                Result.success(message)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to update branding settings"
                Log.e("BrandingRepository", "Error updating branding settings: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("BrandingRepository", "Exception updating branding settings: ${e.message}", e)
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

