package com.ecommercestarter.admin.presentation.products.variants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.api.ProductApiService
import com.ecommercestarter.admin.data.model.ProductVariant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VariantListViewModel @Inject constructor(
    private val productApiService: ProductApiService
) : ViewModel() {

    private val _variantsState = MutableStateFlow<VariantListState>(VariantListState.Loading)
    val variantsState: StateFlow<VariantListState> = _variantsState.asStateFlow()

    fun loadVariants(productId: Int) {
        viewModelScope.launch {
            _variantsState.value = VariantListState.Loading
            try {
                val response = productApiService.getVariants(productId)
                if (response.isSuccessful && response.body() != null) {
                    _variantsState.value = VariantListState.Success(response.body()!!.variants)
                } else {
                    _variantsState.value = VariantListState.Error("Failed to load variants")
                }
            } catch (e: Exception) {
                _variantsState.value = VariantListState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class VariantListState {
    object Loading : VariantListState()
    data class Success(val variants: List<ProductVariant>) : VariantListState()
    data class Error(val message: String) : VariantListState()
}
