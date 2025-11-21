package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("iconClass")
    val iconClass: String = "bi-tag",
    
    @SerializedName("isEnabled")
    val isEnabled: Boolean = true,
    
    @SerializedName("displayOrder")
    val displayOrder: Int = 0,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    
    @SerializedName("subCategories")
    val subCategories: List<SubCategoryDto>? = null
)

data class SubCategoryDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("iconClass")
    val iconClass: String = "bi-tag-fill",
    
    @SerializedName("isEnabled")
    val isEnabled: Boolean = true,
    
    @SerializedName("displayOrder")
    val displayOrder: Int = 0,
    
    @SerializedName("categoryId")
    val categoryId: Int,
    
    @SerializedName("categoryName")
    val categoryName: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class CreateCategoryRequest(
    val name: String,
    val description: String? = null,
    val iconClass: String? = null,
    val isEnabled: Boolean? = true,
    val displayOrder: Int? = 0
)

data class UpdateCategoryRequest(
    val name: String? = null,
    val description: String? = null,
    val iconClass: String? = null,
    val isEnabled: Boolean? = null,
    val displayOrder: Int? = null
)
