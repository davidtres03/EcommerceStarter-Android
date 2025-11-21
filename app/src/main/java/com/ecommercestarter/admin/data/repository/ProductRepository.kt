package com.ecommercestarter.admin.data.repository

import com.ecommercestarter.admin.data.api.ProductApiService
import com.ecommercestarter.admin.data.model.Product
import com.ecommercestarter.admin.data.model.ProductListResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productApiService: ProductApiService
) {
    suspend fun getProducts(
        page: Int = 1,
        pageSize: Int = 20,
        search: String? = null,
        category: String? = null
    ): Response<ProductListResponse> {
        return productApiService.getProducts(page, pageSize, search, category)
    }
    
    suspend fun getProduct(productId: Int): Response<Product> {
        return productApiService.getProduct(productId)
    }
    
    suspend fun createProduct(product: Product): Response<Product> {
        return productApiService.createProduct(product)
    }
    
    suspend fun updateProduct(productId: Int, product: Product): Response<Product> {
        return productApiService.updateProduct(productId, product)
    }
    
    suspend fun deleteProduct(productId: Int): Response<Unit> {
        return productApiService.deleteProduct(productId)
    }
}
