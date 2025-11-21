package com.ecommercestarter.admin.presentation.customers

import android.util.Log
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

sealed class CustomersState {
    object Loading : CustomersState()
    data class Success(val customers: List<Customer>, val totalCount: Int) : CustomersState()
    data class Error(val message: String) : CustomersState()
}

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val customerApiService: CustomerApiService
) : ViewModel() {

    private val _customersState = MutableStateFlow<CustomersState>(CustomersState.Loading)
    val customersState: StateFlow<CustomersState> = _customersState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadCustomers()
    }

    fun loadCustomers(page: Int = 1, pageSize: Int = 20) {
        viewModelScope.launch {
            _customersState.value = CustomersState.Loading
            try {
                val response = customerApiService.getCustomers(
                    page = page,
                    pageSize = pageSize,
                    search = _searchQuery.value.takeIf { it.isNotBlank() }
                )

                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        _customersState.value = CustomersState.Success(
                            customers = data.customers,
                            totalCount = data.totalCount
                        )
                    } else {
                        _customersState.value = CustomersState.Error("No data received")
                    }
                } else {
                    _customersState.value = CustomersState.Error(
                        "Failed to load customers: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("CustomersViewModel", "Error loading customers", e)
                _customersState.value = CustomersState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun searchCustomers() {
        loadCustomers()
    }

    fun refresh() {
        loadCustomers()
    }
}
