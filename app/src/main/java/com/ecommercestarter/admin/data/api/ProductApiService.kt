package com.ecommercestarter.admin.data.api

import com.ecommercestarter.admin.data.model.Product
import com.ecommercestarter.admin.data.model.ProductListResponse
import com.ecommercestarter.admin.data.model.ProductVariant
import com.ecommercestarter.admin.data.model.ProductVariantRequest
import com.ecommercestarter.admin.data.model.VariantListResponse
import retrofit2.Response
import retrofit2.http.*

interface ProductApiService {
    
    @GET("api/products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null
    ): Response<ProductListResponse>
    
    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<Product>
    
    @POST("api/products")
    suspend fun createProduct(@Body product: Product): Response<Product>
    
    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: Product
    ): Response<Product>
    
    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>
    
    // Variant endpoints
    @GET("api/admin/products/{productId}/variants")
    suspend fun getVariants(@Path("productId") productId: Int): Response<VariantListResponse>
    
    @GET("api/admin/products/{productId}/variants/{id}")
    suspend fun getVariant(
        @Path("productId") productId: Int,
        @Path("id") id: Int
    ): Response<ProductVariant>
    
    @POST("api/admin/products/{productId}/variants")
    suspend fun createVariant(
        @Path("productId") productId: Int,
        @Body request: ProductVariantRequest
    ): Response<ProductVariant>
    
    @PUT("api/admin/products/{productId}/variants/{id}")
    suspend fun updateVariant(
        @Path("productId") productId: Int,
        @Path("id") id: Int,
        @Body request: ProductVariantRequest
    ): Response<ProductVariant>
    
    @DELETE("api/admin/products/{productId}/variants/{id}")
    suspend fun deleteVariant(
        @Path("productId") productId: Int,
        @Path("id") id: Int
    ): Response<Unit>
}

