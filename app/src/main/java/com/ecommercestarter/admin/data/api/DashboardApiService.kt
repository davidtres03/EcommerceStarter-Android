package com.ecommercestarter.admin.data.api

import com.ecommercestarter.admin.data.model.DashboardResponse
import retrofit2.Response
import retrofit2.http.GET

interface DashboardApiService {
    
    @GET("api/mobile/dashboard")
    suspend fun getDashboard(): Response<DashboardResponse>
}
