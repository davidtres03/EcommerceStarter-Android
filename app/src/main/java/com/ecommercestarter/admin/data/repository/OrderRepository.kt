package com.ecommercestarter.admin.data.repository

import com.ecommercestarter.admin.data.api.OrderApiService
import com.ecommercestarter.admin.data.model.Order
import com.ecommercestarter.admin.data.model.OrderListResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderApiService: OrderApiService
) {
    suspend fun getOrders(
        page: Int = 1,
        pageSize: Int = 20,
        status: String? = null,
        search: String? = null
    ): Response<OrderListResponse> {
        return orderApiService.getOrders(page, pageSize, status, search)
    }
    
    suspend fun getOrder(orderId: Int): Response<Order> {
        return orderApiService.getOrder(orderId)
    }
    
    suspend fun updateOrderStatus(orderId: Int, status: String): Response<Order> {
        return orderApiService.updateOrderStatus(orderId, status)
    }
}
