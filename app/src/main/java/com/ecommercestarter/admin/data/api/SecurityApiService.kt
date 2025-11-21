package com.ecommercestarter.admin.data.api

import com.ecommercestarter.admin.data.model.SecuritySettingsResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Retrofit API service for security settings
 */
interface SecurityApiService {
    
    /**
     * Get security settings
     */
    @GET("api/settings/security")
    suspend fun getSecuritySettings(): Response<SecuritySettingsResponse>
}
