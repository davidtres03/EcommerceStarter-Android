package com.ecommercestarter.admin.data.api

import com.ecommercestarter.admin.data.model.Order
import com.ecommercestarter.admin.data.model.OrderListResponse
import retrofit2.Response
import retrofit2.http.*

interface OrderApiService {
    
    @GET("api/orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("status") status: String? = null,
        @Query("search") search: String? = null
    ): Response<OrderListResponse>
    
    @GET("api/orders/{id}")
    suspend fun getOrder(@Path("id") id: Int): Response<Order>
    
    @PUT("api/orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: Int,
        @Query("status") status: String
    ): Response<Order>
    
    @DELETE("api/orders/{id}")
    suspend fun deleteOrder(@Path("id") id: Int): Response<Unit>
}
