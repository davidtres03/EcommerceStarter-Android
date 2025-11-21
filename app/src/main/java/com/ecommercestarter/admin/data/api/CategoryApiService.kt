package com.ecommercestarter.admin.data.api

import com.ecommercestarter.admin.data.model.CategoryDto
import com.ecommercestarter.admin.data.model.CreateCategoryRequest
import com.ecommercestarter.admin.data.model.UpdateCategoryRequest
import retrofit2.Response
import retrofit2.http.*

interface CategoryApiService {
    
    @GET("api/categories")
    suspend fun getCategories(
        @Query("includeDisabled") includeDisabled: Boolean = false
    ): Response<CategoryListResponse>
    
    @GET("api/categories/{id}")
    suspend fun getCategory(
        @Path("id") id: Int
    ): Response<CategoryResponse>
    
    @POST("api/categories")
    suspend fun createCategory(
        @Body request: CreateCategoryRequest
    ): Response<CategoryResponse>
    
    @PUT("api/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body request: UpdateCategoryRequest
    ): Response<CategoryResponse>
    
    @DELETE("api/categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: Int
    ): Response<DeleteResponse>
    
    @POST("api/categories/{categoryId}/subcategories")
    suspend fun createSubCategory(
        @Path("categoryId") categoryId: Int,
        @Body request: CreateCategoryRequest
    ): Response<CategoryResponse>
    
    @PUT("api/subcategories/{id}")
    suspend fun updateSubCategory(
        @Path("id") id: Int,
        @Body request: UpdateCategoryRequest
    ): Response<CategoryResponse>
    
    @DELETE("api/subcategories/{id}")
    suspend fun deleteSubCategory(
        @Path("id") id: Int
    ): Response<DeleteResponse>
}

data class CategoryListResponse(
    val success: Boolean,
    val data: List<CategoryDto>,
    val count: Int
)

data class CategoryResponse(
    val success: Boolean,
    val message: String? = null,
    val data: CategoryDto
)

data class DeleteResponse(
    val success: Boolean,
    val message: String,
    val productCount: Int? = null,
    val subcategoryCount: Int? = null
)
