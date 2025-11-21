package com.ecommercestarter.admin.data.repository

import com.ecommercestarter.admin.data.api.DashboardApiService
import com.ecommercestarter.admin.data.model.DashboardResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val dashboardApiService: DashboardApiService
) {
    suspend fun getDashboard(): Response<DashboardResponse> {
        return dashboardApiService.getDashboard()
    }
}
