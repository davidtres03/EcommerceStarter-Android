package com.ecommercestarter.admin.presentation.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.api.OrderApiService
import com.ecommercestarter.admin.data.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OrdersState {
    object Loading : OrdersState()
    data class Success(val orders: List<Order>, val totalCount: Int) : OrdersState()
    data class Error(val message: String) : OrdersState()
}

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderApiService: OrderApiService
) : ViewModel() {

    private val _ordersState = MutableStateFlow<OrdersState>(OrdersState.Loading)
    val ordersState: StateFlow<OrdersState> = _ordersState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter: StateFlow<String?> = _statusFilter.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders(page: Int = 1, pageSize: Int = 20) {
        viewModelScope.launch {
            _ordersState.value = OrdersState.Loading
            try {
                val response = orderApiService.getOrders(
                    page = page,
                    pageSize = pageSize,
                    status = _statusFilter.value,
                    search = _searchQuery.value.takeIf { it.isNotBlank() }
                )

                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        _ordersState.value = OrdersState.Success(
                            orders = data.orders,
                            totalCount = data.totalCount
                        )
                    } else {
                        _ordersState.value = OrdersState.Error("No data received")
                    }
                } else {
                    _ordersState.value = OrdersState.Error(
                        "Failed to load orders: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("OrdersViewModel", "Error loading orders", e)
                _ordersState.value = OrdersState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChange(status: String?) {
        _statusFilter.value = status
        loadOrders()
    }

    fun searchOrders() {
        loadOrders()
    }

    fun refresh() {
        loadOrders()
    }
}
