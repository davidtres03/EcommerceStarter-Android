package com.ecommercestarter.admin.data.api

import com.ecommercestarter.admin.data.model.ApiConfigurationsResponse
import com.ecommercestarter.admin.data.model.ToggleApiConfigResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit API service for managing API configurations
 */
interface ApiConfigApiService {
    
    /**
     * Get all API configurations grouped by type
     */
    @GET("api/settings/api-configurations")
    suspend fun getApiConfigurations(): Response<ApiConfigurationsResponse>
    
    /**
     * Toggle an API configuration active/inactive
     */
    @PUT("api/settings/api-configurations/{id}/toggle")
    suspend fun toggleApiConfiguration(@Path("id") configId: Int): Response<ToggleApiConfigResponse>
}
