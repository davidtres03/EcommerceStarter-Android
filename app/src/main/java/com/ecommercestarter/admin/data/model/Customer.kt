package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

data class Customer(
    @SerializedName("id")
    val id: String,  // Changed from Int to String - API returns GUID
    
    @SerializedName("firstName")
    val firstName: String,
    
    @SerializedName("lastName")
    val lastName: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("phone")
    val phone: String?,
    
    @SerializedName("address")
    val address: String?,
    
    @SerializedName("city")
    val city: String?,
    
    @SerializedName("state")
    val state: String?,
    
    @SerializedName("zipCode")
    val zipCode: String?,
    
    @SerializedName("country")
    val country: String?,
    
    @SerializedName("totalOrders")
    val totalOrders: Int = 0,
    
    @SerializedName("totalSpent")
    val totalSpent: Double = 0.0,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("lastOrderDate")
    val lastOrderDate: String?
)

data class CustomerListResponse(
    @SerializedName("customers")
    val customers: List<Customer>,
    
    @SerializedName("totalCount")
    val totalCount: Int,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("pageSize")
    val pageSize: Int
)
