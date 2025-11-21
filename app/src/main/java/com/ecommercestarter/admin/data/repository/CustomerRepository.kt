package com.ecommercestarter.admin.data.repository

import com.ecommercestarter.admin.data.api.CustomerApiService
import com.ecommercestarter.admin.data.model.Customer
import com.ecommercestarter.admin.data.model.CustomerListResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val customerApiService: CustomerApiService
) {
    
    suspend fun getCustomers(
        page: Int = 1,
        pageSize: Int = 20,
        search: String? = null
    ): Response<CustomerListResponse> {
        return customerApiService.getCustomers(page, pageSize, search)
    }
    
    suspend fun getCustomer(id: String): Response<Customer> {
        return customerApiService.getCustomer(id.toIntOrNull() ?: 0)
    }
    
    suspend fun createCustomer(customer: Customer): Response<Customer> {
        return customerApiService.createCustomer(customer)
    }
    
    suspend fun updateCustomer(id: String, customer: Customer): Response<Customer> {
        return customerApiService.updateCustomer(id.toIntOrNull() ?: 0, customer)
    }
    
    suspend fun deleteCustomer(id: String): Response<Unit> {
        return customerApiService.deleteCustomer(id.toIntOrNull() ?: 0)
    }
}
