package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("orderNumber")
    val orderNumber: String,
    
    @SerializedName("customerId")
    val customerId: Int,
    
    @SerializedName("customerName")
    val customerName: String,
    
    @SerializedName("customerEmail")
    val customerEmail: String,
    
    @SerializedName("status")
    val status: String, // Pending, Processing, Shipped, Delivered, Cancelled
    
    @SerializedName("totalAmount")
    val totalAmount: Double,
    
    @SerializedName("itemCount")
    val itemCount: Int,
    
    @SerializedName("shippingAddress")
    val shippingAddress: String?,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String?,
    
    @SerializedName("deliveredAt")
    val deliveredAt: String?
)

data class OrderListResponse(
    @SerializedName("orders")
    val orders: List<Order>,
    
    @SerializedName("totalCount")
    val totalCount: Int,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("pageSize")
    val pageSize: Int
)
