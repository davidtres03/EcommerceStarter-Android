package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("stockQuantity")
    val stockQuantity: Int,
    
    @SerializedName("imageUrl")
    val imageUrl: String?,
    
    @SerializedName("category")
    val category: String?,
    
    @SerializedName("isActive")
    val isActive: Boolean = true,
    
    @SerializedName("hasVariants")
    val hasVariants: Boolean = false,
    
    @SerializedName("variants")
    val variants: List<ProductVariant>? = null,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("updatedAt")
    val updatedAt: String?
)

data class ProductListResponse(
    @SerializedName("products")
    val products: List<Product>,
    
    @SerializedName("totalCount")
    val totalCount: Int,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("pageSize")
    val pageSize: Int
)
