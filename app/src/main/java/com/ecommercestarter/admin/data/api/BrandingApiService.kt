package com.ecommercestarter.admin.data.api

import com.ecommercestarter.admin.data.model.BrandingConfig
import com.ecommercestarter.admin.data.model.BrandingSettingsResponse
import com.ecommercestarter.admin.data.model.BrandingUpdateResponse
import com.ecommercestarter.admin.data.model.UpdateBrandingRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface BrandingApiService {
    
    @GET("api/branding")
    suspend fun getBranding(): Response<BrandingConfig>
    
    @GET("api/settings/branding")
    suspend fun getBrandingSettings(): Response<BrandingSettingsResponse>
    
    @PUT("api/settings/branding")
    suspend fun updateBrandingSettings(@Body request: UpdateBrandingRequest): Response<BrandingUpdateResponse>
}
