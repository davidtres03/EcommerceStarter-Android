package com.ecommercestarter.admin.data.api

import com.ecommercestarter.admin.data.model.BrandingConfig
import retrofit2.Response
import retrofit2.http.GET

interface BrandingApiService {
    
    @GET("api/branding")
    suspend fun getBranding(): Response<BrandingConfig>
}
