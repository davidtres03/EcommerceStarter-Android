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

@HiltViewModel
class ProductCreateViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductCreateUiState>(ProductCreateUiState.Idle)
    val uiState: StateFlow<ProductCreateUiState> = _uiState.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _price = MutableStateFlow("")
    val price: StateFlow<String> = _price.asStateFlow()

    private val _stockQuantity = MutableStateFlow("")
    val stockQuantity: StateFlow<String> = _stockQuantity.asStateFlow()

    private val _imageUrl = MutableStateFlow("")
    val imageUrl: StateFlow<String> = _imageUrl.asStateFlow()

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _isActive = MutableStateFlow(true)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onDescriptionChange(value: String) {
        _description.value = value
    }

    fun onPriceChange(value: String) {
        // Only allow valid decimal numbers
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _price.value = value
        }
    }

    fun onStockQuantityChange(value: String) {
        // Only allow integers
        if (value.isEmpty() || value.matches(Regex("^\\d+$"))) {
            _stockQuantity.value = value
        }
    }

    fun onImageUrlChange(value: String) {
        _imageUrl.value = value
    }

    fun onCategoryChange(value: String) {
        _category.value = value
    }

    fun onIsActiveChange(value: Boolean) {
        _isActive.value = value
    }

    fun createProduct() {
        // Validate inputs
        if (_name.value.isBlank()) {
            _uiState.value = ProductCreateUiState.Error("Product name is required")
            return
        }

        val priceValue = _price.value.toDoubleOrNull()
        if (priceValue == null || priceValue <= 0) {
            _uiState.value = ProductCreateUiState.Error("Valid price is required")
            return
        }

        val stockValue = _stockQuantity.value.toIntOrNull()
        if (stockValue == null || stockValue < 0) {
            _uiState.value = ProductCreateUiState.Error("Valid stock quantity is required")
            return
        }

        _uiState.value = ProductCreateUiState.Loading

        viewModelScope.launch {
            try {
                val product = Product(
                    id = 0, // Will be set by backend
                    name = _name.value.trim(),
                    description = _description.value.trim().ifBlank { null },
                    price = priceValue,
                    stockQuantity = stockValue,
                    imageUrl = _imageUrl.value.trim().ifBlank { null },
                    category = _category.value.trim().ifBlank { null },
                    isActive = _isActive.value,
                    createdAt = null,
                    updatedAt = null
                )

                val response = productRepository.createProduct(product)

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProductCreateUiState.Success(response.body()!!)
                } else {
                    _uiState.value = ProductCreateUiState.Error(
                        response.message() ?: "Failed to create product"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ProductCreateUiState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun clearError() {
        if (_uiState.value is ProductCreateUiState.Error) {
            _uiState.value = ProductCreateUiState.Idle
        }
    }
}

sealed class ProductCreateUiState {
    object Idle : ProductCreateUiState()
    object Loading : ProductCreateUiState()
    data class Success(val product: Product) : ProductCreateUiState()
    data class Error(val message: String) : ProductCreateUiState()
}
