package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

data class ProductVariant(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("productId")
    val productId: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("sku")
    val sku: String?,
    
    @SerializedName("stockQuantity")
    val stockQuantity: Int,
    
    @SerializedName("imageUrl")
    val imageUrl: String?,
    
    @SerializedName("priceOverride")
    val priceOverride: Double?,
    
    @SerializedName("isAvailable")
    val isAvailable: Boolean = true,
    
    @SerializedName("isFeatured")
    val isFeatured: Boolean = false,
    
    @SerializedName("displayOrder")
    val displayOrder: Int = 0,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("updatedAt")
    val updatedAt: String?
)

data class ProductVariantRequest(
    val name: String,
    val sku: String? = null,
    val stockQuantity: Int,
    val imageUrl: String? = null,
    val priceOverride: Double? = null,
    val isAvailable: Boolean = true,
    val isFeatured: Boolean = false,
    val displayOrder: Int = 0
)

data class VariantListResponse(
    @SerializedName("variants")
    val variants: List<ProductVariant>,
    
    @SerializedName("totalCount")
    val totalCount: Int
)
