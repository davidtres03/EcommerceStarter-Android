package com.ecommercestarter.admin.presentation.products.variants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.api.ProductApiService
import com.ecommercestarter.admin.data.model.ProductVariantRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VariantFormViewModel @Inject constructor(
    private val productApiService: ProductApiService
) : ViewModel() {

    private val _formState = MutableStateFlow<VariantFormState>(VariantFormState.Ready())
    val formState: StateFlow<VariantFormState> = _formState.asStateFlow()

    private val _saveState = MutableStateFlow<VariantSaveState>(VariantSaveState.Idle)
    val saveState: StateFlow<VariantSaveState> = _saveState.asStateFlow()

    fun loadVariant(productId: Int, variantId: Int) {
        viewModelScope.launch {
            _formState.value = VariantFormState.Loading
            try {
                val response = productApiService.getVariant(productId, variantId)
                if (response.isSuccessful && response.body() != null) {
                    val variant = response.body()!!
                    _formState.value = VariantFormState.Ready(
                        name = variant.name,
                        sku = variant.sku ?: "",
                        stockQuantity = variant.stockQuantity.toString(),
                        priceOverride = variant.priceOverride?.toString() ?: "",
                        imageUrl = variant.imageUrl ?: "",
                        isAvailable = variant.isAvailable,
                        isFeatured = variant.isFeatured,
                        displayOrder = variant.displayOrder.toString()
                    )
                } else {
                    _formState.value = VariantFormState.Error("Failed to load variant")
                }
            } catch (e: Exception) {
                _formState.value = VariantFormState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateName(name: String) {
        val currentState = (_formState.value as? VariantFormState.Ready) ?: return
        _formState.value = currentState.copy(
            name = name,
            nameError = if (name.isBlank()) "Name is required" else null
        )
    }

    fun updateSku(sku: String) {
        val currentState = (_formState.value as? VariantFormState.Ready) ?: return
        _formState.value = currentState.copy(sku = sku)
    }

    fun updateStockQuantity(stock: String) {
        val currentState = (_formState.value as? VariantFormState.Ready) ?: return
        val error = when {
            stock.isBlank() -> "Stock quantity is required"
            stock.toIntOrNull() == null -> "Must be a valid number"
            stock.toInt() < 0 -> "Cannot be negative"
            else -> null
        }
        _formState.value = currentState.copy(stockQuantity = stock, stockError = error)
    }

    fun updatePriceOverride(price: String) {
        val currentState = (_formState.value as? VariantFormState.Ready) ?: return
        val error = if (price.isNotBlank() && price.toDoubleOrNull() == null) {
            "Must be a valid price"
        } else null
        _formState.value = currentState.copy(priceOverride = price, priceError = error)
    }

    fun updateImageUrl(url: String) {
        val currentState = (_formState.value as? VariantFormState.Ready) ?: return
        _formState.value = currentState.copy(imageUrl = url)
    }

    fun updateDisplayOrder(order: String) {
        val currentState = (_formState.value as? VariantFormState.Ready) ?: return
        _formState.value = currentState.copy(displayOrder = order)
    }

    fun updateIsAvailable(available: Boolean) {
        val currentState = (_formState.value as? VariantFormState.Ready) ?: return
        _formState.value = currentState.copy(isAvailable = available)
    }

    fun updateIsFeatured(featured: Boolean) {
        val currentState = (_formState.value as? VariantFormState.Ready) ?: return
        _formState.value = currentState.copy(isFeatured = featured)
    }

    fun saveVariant(productId: Int, variantId: Int?) {
        val currentState = (_formState.value as? VariantFormState.Ready) ?: return

        // Validation
        if (currentState.name.isBlank()) {
            _formState.value = currentState.copy(nameError = "Name is required")
            return
        }
        if (currentState.stockQuantity.isBlank() || currentState.stockQuantity.toIntOrNull() == null) {
            _formState.value = currentState.copy(stockError = "Valid stock quantity required")
            return
        }
        if (currentState.priceOverride.isNotBlank() && currentState.priceOverride.toDoubleOrNull() == null) {
            _formState.value = currentState.copy(priceError = "Invalid price")
            return
        }

        viewModelScope.launch {
            _saveState.value = VariantSaveState.Saving
            try {
                val request = ProductVariantRequest(
                    name = currentState.name,
                    sku = currentState.sku.ifBlank { null },
                    stockQuantity = currentState.stockQuantity.toInt(),
                    priceOverride = currentState.priceOverride.toDoubleOrNull(),
                    imageUrl = currentState.imageUrl.ifBlank { null },
                    isAvailable = currentState.isAvailable,
                    isFeatured = currentState.isFeatured,
                    displayOrder = currentState.displayOrder.toIntOrNull() ?: 0
                )

                val response = if (variantId == null) {
                    productApiService.createVariant(productId, request)
                } else {
                    productApiService.updateVariant(productId, variantId, request)
                }

                if (response.isSuccessful) {
                    _saveState.value = VariantSaveState.Success
                } else {
                    _saveState.value = VariantSaveState.Error("Failed to save variant")
                }
            } catch (e: Exception) {
                _saveState.value = VariantSaveState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteVariant(productId: Int, variantId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = productApiService.deleteVariant(productId, variantId)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Failed to delete variant")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class VariantFormState {
    object Loading : VariantFormState()
    data class Ready(
        val name: String = "",
        val sku: String = "",
        val stockQuantity: String = "0",
        val priceOverride: String = "",
        val imageUrl: String = "",
        val isAvailable: Boolean = true,
        val isFeatured: Boolean = false,
        val displayOrder: String = "0",
        val nameError: String? = null,
        val stockError: String? = null,
        val priceError: String? = null
    ) : VariantFormState()
    data class Error(val message: String) : VariantFormState()
}

sealed class VariantSaveState {
    object Idle : VariantSaveState()
    object Saving : VariantSaveState()
    object Success : VariantSaveState()
    data class Error(val message: String) : VariantSaveState()
}
