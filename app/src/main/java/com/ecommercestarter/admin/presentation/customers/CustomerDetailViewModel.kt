package com.ecommercestarter.admin.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.api.CustomerApiService
import com.ecommercestarter.admin.data.model.Customer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    private val customerApiService: CustomerApiService
) : ViewModel() {

    private val _customerState = MutableStateFlow<CustomerDetailState>(CustomerDetailState.Loading)
    val customerState: StateFlow<CustomerDetailState> = _customerState.asStateFlow()

    fun loadCustomer(customerId: String) {
        viewModelScope.launch {
            _customerState.value = CustomerDetailState.Loading
            try {
                val response = customerApiService.getCustomer(customerId.toInt())
                if (response.isSuccessful && response.body() != null) {
                    _customerState.value = CustomerDetailState.Success(response.body()!!)
                } else {
                    _customerState.value = CustomerDetailState.Error("Failed to load customer")
                }
            } catch (e: Exception) {
                _customerState.value = CustomerDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteCustomer(customerId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = customerApiService.deleteCustomer(customerId.toInt())
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Failed to delete customer")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class CustomerDetailState {
    object Loading : CustomerDetailState()
    data class Success(val customer: Customer) : CustomerDetailState()
    data class Error(val message: String) : CustomerDetailState()
}
