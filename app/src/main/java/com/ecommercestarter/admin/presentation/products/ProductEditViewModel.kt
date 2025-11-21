package com.ecommercestarter.admin.presentation.products

import androidx.lifecycle.SavedStateHandle
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
class ProductEditViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = savedStateHandle.get<Int>("productId") ?: 0

    private val _uiState = MutableStateFlow<ProductEditUiState>(ProductEditUiState.Loading)
    val uiState: StateFlow<ProductEditUiState> = _uiState.asStateFlow()

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

    private var originalProduct: Product? = null

    init {
        loadProduct()
    }

    private fun loadProduct() {
        _uiState.value = ProductEditUiState.Loading
        viewModelScope.launch {
            try {
                val response = productRepository.getProduct(productId)
                if (response.isSuccessful && response.body() != null) {
                    val product = response.body()!!
                    originalProduct = product
                    
                    _name.value = product.name
                    _description.value = product.description ?: ""
                    _price.value = product.price.toString()
                    _stockQuantity.value = product.stockQuantity.toString()
                    _imageUrl.value = product.imageUrl ?: ""
                    _category.value = product.category ?: ""
                    _isActive.value = product.isActive
                    
                    _uiState.value = ProductEditUiState.Loaded(product)
                } else {
                    _uiState.value = ProductEditUiState.Error("Failed to load product")
                }
            } catch (e: Exception) {
                _uiState.value = ProductEditUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onDescriptionChange(value: String) {
        _description.value = value
    }

    fun onPriceChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _price.value = value
        }
    }

    fun onStockQuantityChange(value: String) {
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

    fun updateProduct() {
        val original = originalProduct ?: return
        
        // Validate inputs
        if (_name.value.isBlank()) {
            _uiState.value = ProductEditUiState.Error("Product name is required")
            return
        }

        val priceValue = _price.value.toDoubleOrNull()
        if (priceValue == null || priceValue <= 0) {
            _uiState.value = ProductEditUiState.Error("Valid price is required")
            return
        }

        val stockValue = _stockQuantity.value.toIntOrNull()
        if (stockValue == null || stockValue < 0) {
            _uiState.value = ProductEditUiState.Error("Valid stock quantity is required")
            return
        }

        _uiState.value = ProductEditUiState.Saving

        viewModelScope.launch {
            try {
                val updatedProduct = original.copy(
                    name = _name.value.trim(),
                    description = _description.value.trim().ifBlank { null },
                    price = priceValue,
                    stockQuantity = stockValue,
                    imageUrl = _imageUrl.value.trim().ifBlank { null },
                    category = _category.value.trim().ifBlank { null },
                    isActive = _isActive.value
                )

                val response = productRepository.updateProduct(productId, updatedProduct)

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProductEditUiState.Success(response.body()!!)
                } else {
                    _uiState.value = ProductEditUiState.Error(
                        response.message() ?: "Failed to update product"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ProductEditUiState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun clearError() {
        val current = _uiState.value
        if (current is ProductEditUiState.Error && originalProduct != null) {
            _uiState.value = ProductEditUiState.Loaded(originalProduct!!)
        }
    }
}

sealed class ProductEditUiState {
    object Loading : ProductEditUiState()
    data class Loaded(val product: Product) : ProductEditUiState()
    object Saving : ProductEditUiState()
    data class Success(val product: Product) : ProductEditUiState()
    data class Error(val message: String) : ProductEditUiState()
}
