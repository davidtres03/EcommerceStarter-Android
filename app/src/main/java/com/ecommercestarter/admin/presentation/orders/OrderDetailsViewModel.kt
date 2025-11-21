package com.ecommercestarter.admin.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.model.Order
import com.ecommercestarter.admin.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OrderDetailsState {
    object Loading : OrderDetailsState()
    data class Success(val order: Order) : OrderDetailsState()
    data class Error(val message: String) : OrderDetailsState()
}

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orderState = MutableStateFlow<OrderDetailsState>(OrderDetailsState.Loading)
    val orderState: StateFlow<OrderDetailsState> = _orderState.asStateFlow()
    
    private val _statusUpdateState = MutableStateFlow<StatusUpdateState>(StatusUpdateState.Idle)
    val statusUpdateState: StateFlow<StatusUpdateState> = _statusUpdateState.asStateFlow()

    fun loadOrder(orderId: Int) {
        viewModelScope.launch {
            _orderState.value = OrderDetailsState.Loading
            try {
                val response = orderRepository.getOrder(orderId)
                if (response.isSuccessful && response.body() != null) {
                    _orderState.value = OrderDetailsState.Success(response.body()!!)
                } else {
                    _orderState.value = OrderDetailsState.Error(
                        response.message() ?: "Failed to load order"
                    )
                }
            } catch (e: Exception) {
                _orderState.value = OrderDetailsState.Error(
                    e.message ?: "An unexpected error occurred"
                )
            }
        }
    }
    
    fun updateOrderStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            _statusUpdateState.value = StatusUpdateState.Loading
            try {
                val response = orderRepository.updateOrderStatus(orderId, status)
                if (response.isSuccessful && response.body() != null) {
                    _statusUpdateState.value = StatusUpdateState.Success
                    // Reload the order to get updated data
                    loadOrder(orderId)
                } else {
                    _statusUpdateState.value = StatusUpdateState.Error(
                        response.message() ?: "Failed to update status"
                    )
                }
            } catch (e: Exception) {
                _statusUpdateState.value = StatusUpdateState.Error(
                    e.message ?: "An unexpected error occurred"
                )
            }
        }
    }
    
    fun clearStatusUpdateState() {
        _statusUpdateState.value = StatusUpdateState.Idle
    }
}

sealed class StatusUpdateState {
    object Idle : StatusUpdateState()
    object Loading : StatusUpdateState()
    object Success : StatusUpdateState()
    data class Error(val message: String) : StatusUpdateState()
}
