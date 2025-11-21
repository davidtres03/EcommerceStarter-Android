package com.ecommercestarter.admin.data.api

import com.ecommercestarter.admin.data.model.Customer
import com.ecommercestarter.admin.data.model.CustomerListResponse
import retrofit2.Response
import retrofit2.http.*

interface CustomerApiService {
    
    @GET("api/customers")
    suspend fun getCustomers(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("search") search: String? = null
    ): Response<CustomerListResponse>
    
    @GET("api/customers/{id}")
    suspend fun getCustomer(@Path("id") id: Int): Response<Customer>
    
    @POST("api/customers")
    suspend fun createCustomer(@Body customer: Customer): Response<Customer>
    
    @PUT("api/customers/{id}")
    suspend fun updateCustomer(
        @Path("id") id: Int,
        @Body customer: Customer
    ): Response<Customer>
    
    @DELETE("api/customers/{id}")
    suspend fun deleteCustomer(@Path("id") id: Int): Response<Unit>
}
