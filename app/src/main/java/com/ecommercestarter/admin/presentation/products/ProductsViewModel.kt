package com.ecommercestarter.admin.presentation.products

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.api.ProductApiService
import com.ecommercestarter.admin.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProductsState {
    object Loading : ProductsState()
    data class Success(val products: List<Product>, val totalCount: Int) : ProductsState()
    data class Error(val message: String) : ProductsState()
}

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productApiService: ProductApiService
) : ViewModel() {

    private val _productsState = MutableStateFlow<ProductsState>(ProductsState.Loading)
    val productsState: StateFlow<ProductsState> = _productsState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts(page: Int = 1, pageSize: Int = 20) {
        viewModelScope.launch {
            _productsState.value = ProductsState.Loading
            try {
                val response = productApiService.getProducts(
                    page = page,
                    pageSize = pageSize,
                    search = _searchQuery.value.takeIf { it.isNotBlank() }
                )

                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        _productsState.value = ProductsState.Success(
                            products = data.products,
                            totalCount = data.totalCount
                        )
                    } else {
                        _productsState.value = ProductsState.Error("No data received")
                    }
                } else {
                    _productsState.value = ProductsState.Error(
                        "Failed to load products: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("ProductsViewModel", "Error loading products", e)
                _productsState.value = ProductsState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun searchProducts() {
        loadProducts()
    }

    fun refresh() {
        loadProducts()
    }
}
