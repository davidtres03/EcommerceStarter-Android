package com.ecommercestarter.admin.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.model.Product
import com.ecommercestarter.admin.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProductDetailsState {
    object Loading : ProductDetailsState()
    data class Success(val product: Product) : ProductDetailsState()
    data class Error(val message: String) : ProductDetailsState()
}

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _productState = MutableStateFlow<ProductDetailsState>(ProductDetailsState.Loading)
    val productState: StateFlow<ProductDetailsState> = _productState.asStateFlow()

    fun loadProduct(productId: Int) {
        viewModelScope.launch {
            _productState.value = ProductDetailsState.Loading
            try {
                val response = productRepository.getProduct(productId)
                if (response.isSuccessful && response.body() != null) {
                    _productState.value = ProductDetailsState.Success(response.body()!!)
                } else {
                    _productState.value = ProductDetailsState.Error(
                        response.message() ?: "Failed to load product"
                    )
                }
            } catch (e: Exception) {
                _productState.value = ProductDetailsState.Error(
                    e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    fun deleteProduct(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val currentState = _productState.value
            if (currentState is ProductDetailsState.Success) {
                try {
                    val response = productRepository.deleteProduct(currentState.product.id)
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onError(response.message() ?: "Failed to delete product")
                    }
                } catch (e: Exception) {
                    onError(e.message ?: "An unexpected error occurred")
                }
            }
        }
    }
}
