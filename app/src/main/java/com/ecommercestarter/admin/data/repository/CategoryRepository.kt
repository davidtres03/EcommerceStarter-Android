package com.ecommercestarter.admin.data.repository

import android.util.Log
import com.ecommercestarter.admin.data.api.CategoryApiService
import com.ecommercestarter.admin.data.model.CategoryDto
import com.ecommercestarter.admin.data.model.CreateCategoryRequest
import com.ecommercestarter.admin.data.model.UpdateCategoryRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryApiService: CategoryApiService
) {
    
    suspend fun getCategories(includeDisabled: Boolean = false): Result<List<CategoryDto>> {
        return try {
            Log.d("CategoryRepository", "Fetching categories (includeDisabled: $includeDisabled)")
            val response = categoryApiService.getCategories(includeDisabled)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d("CategoryRepository", "Successfully fetched ${body.count} categories")
                    Result.success(body.data)
                } else {
                    Log.e("CategoryRepository", "Failed to fetch categories: ${body?.success}")
                    Result.failure(Exception("Failed to fetch categories"))
                }
            } else {
                Log.e("CategoryRepository", "API error: ${response.code()} - ${response.message()}")
                Result.failure(Exception("API error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error fetching categories", e)
            Result.failure(e)
        }
    }
    
    suspend fun getCategory(id: Int): Result<CategoryDto> {
        return try {
            Log.d("CategoryRepository", "Fetching category $id")
            val response = categoryApiService.getCategory(id)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d("CategoryRepository", "Successfully fetched category: ${body.data.name}")
                    Result.success(body.data)
                } else {
                    Log.e("CategoryRepository", "Failed to fetch category")
                    Result.failure(Exception("Category not found"))
                }
            } else {
                Log.e("CategoryRepository", "API error: ${response.code()}")
                Result.failure(Exception("API error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error fetching category $id", e)
            Result.failure(e)
        }
    }
    
    suspend fun createCategory(request: CreateCategoryRequest): Result<CategoryDto> {
        return try {
            Log.d("CategoryRepository", "Creating category: ${request.name}")
            val response = categoryApiService.createCategory(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d("CategoryRepository", "Category created successfully: ${body.data.name}")
                    Result.success(body.data)
                } else {
                    Log.e("CategoryRepository", "Failed to create category: ${body?.message}")
                    Result.failure(Exception(body?.message ?: "Failed to create category"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("CategoryRepository", "API error: ${response.code()} - $errorBody")
                Result.failure(Exception(errorBody ?: "Failed to create category"))
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error creating category", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateCategory(id: Int, request: UpdateCategoryRequest): Result<CategoryDto> {
        return try {
            Log.d("CategoryRepository", "Updating category $id")
            val response = categoryApiService.updateCategory(id, request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d("CategoryRepository", "Category updated successfully")
                    Result.success(body.data)
                } else {
                    Log.e("CategoryRepository", "Failed to update category: ${body?.message}")
                    Result.failure(Exception(body?.message ?: "Failed to update category"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("CategoryRepository", "API error: ${response.code()} - $errorBody")
                Result.failure(Exception(errorBody ?: "Failed to update category"))
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error updating category $id", e)
            Result.failure(e)
        }
    }
    
    suspend fun deleteCategory(id: Int): Result<String> {
        return try {
            Log.d("CategoryRepository", "Deleting category $id")
            val response = categoryApiService.deleteCategory(id)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d("CategoryRepository", "Category deleted successfully")
                    Result.success(body.message)
                } else {
                    Log.e("CategoryRepository", "Failed to delete category: ${body?.message}")
                    Result.failure(Exception(body?.message ?: "Failed to delete category"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("CategoryRepository", "API error: ${response.code()} - $errorBody")
                Result.failure(Exception(errorBody ?: "Cannot delete category"))
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error deleting category $id", e)
            Result.failure(e)
        }
    }
    
    suspend fun createSubCategory(categoryId: Int, request: CreateCategoryRequest): Result<CategoryDto> {
        return try {
            Log.d("CategoryRepository", "Creating subcategory: ${request.name} for category $categoryId")
            val response = categoryApiService.createSubCategory(categoryId, request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d("CategoryRepository", "Subcategory created successfully")
                    Result.success(body.data)
                } else {
                    Log.e("CategoryRepository", "Failed to create subcategory: ${body?.message}")
                    Result.failure(Exception(body?.message ?: "Failed to create subcategory"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("CategoryRepository", "API error: ${response.code()} - $errorBody")
                Result.failure(Exception(errorBody ?: "Failed to create subcategory"))
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error creating subcategory", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateSubCategory(id: Int, request: UpdateCategoryRequest): Result<CategoryDto> {
        return try {
            Log.d("CategoryRepository", "Updating subcategory $id")
            val response = categoryApiService.updateSubCategory(id, request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d("CategoryRepository", "Subcategory updated successfully")
                    Result.success(body.data)
                } else {
                    Log.e("CategoryRepository", "Failed to update subcategory: ${body?.message}")
                    Result.failure(Exception(body?.message ?: "Failed to update subcategory"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("CategoryRepository", "API error: ${response.code()} - $errorBody")
                Result.failure(Exception(errorBody ?: "Failed to update subcategory"))
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error updating subcategory $id", e)
            Result.failure(e)
        }
    }
    
    suspend fun deleteSubCategory(id: Int): Result<String> {
        return try {
            Log.d("CategoryRepository", "Deleting subcategory $id")
            val response = categoryApiService.deleteSubCategory(id)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d("CategoryRepository", "Subcategory deleted successfully")
                    Result.success(body.message)
                } else {
                    Log.e("CategoryRepository", "Failed to delete subcategory: ${body?.message}")
                    Result.failure(Exception(body?.message ?: "Failed to delete subcategory"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("CategoryRepository", "API error: ${response.code()} - $errorBody")
                Result.failure(Exception(errorBody ?: "Cannot delete subcategory"))
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error deleting subcategory $id", e)
            Result.failure(e)
        }
    }
}
