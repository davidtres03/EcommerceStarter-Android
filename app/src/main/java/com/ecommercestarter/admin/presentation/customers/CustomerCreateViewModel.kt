package com.ecommercestarter.admin.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.model.Customer
import com.ecommercestarter.admin.data.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerCreateViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CustomerCreateUiState>(CustomerCreateUiState.Idle)
    val uiState: StateFlow<CustomerCreateUiState> = _uiState.asStateFlow()

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _city = MutableStateFlow("")
    val city: StateFlow<String> = _city.asStateFlow()

    private val _state = MutableStateFlow("")
    val state: StateFlow<String> = _state.asStateFlow()

    private val _zipCode = MutableStateFlow("")
    val zipCode: StateFlow<String> = _zipCode.asStateFlow()

    private val _country = MutableStateFlow("")
    val country: StateFlow<String> = _country.asStateFlow()

    fun onFirstNameChange(value: String) {
        _firstName.value = value
    }

    fun onLastNameChange(value: String) {
        _lastName.value = value
    }

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPhoneChange(value: String) {
        _phone.value = value
    }

    fun onAddressChange(value: String) {
        _address.value = value
    }

    fun onCityChange(value: String) {
        _city.value = value
    }

    fun onStateChange(value: String) {
        _state.value = value
    }

    fun onZipCodeChange(value: String) {
        _zipCode.value = value
    }

    fun onCountryChange(value: String) {
        _country.value = value
    }

    fun createCustomer() {
        // Validate inputs
        if (_firstName.value.isBlank()) {
            _uiState.value = CustomerCreateUiState.Error("First name is required")
            return
        }

        if (_lastName.value.isBlank()) {
            _uiState.value = CustomerCreateUiState.Error("Last name is required")
            return
        }

        if (_email.value.isBlank()) {
            _uiState.value = CustomerCreateUiState.Error("Email is required")
            return
        }

        // Simple email validation
        if (!_email.value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            _uiState.value = CustomerCreateUiState.Error("Valid email is required")
            return
        }

        _uiState.value = CustomerCreateUiState.Loading

        viewModelScope.launch {
            try {
                val customer = Customer(
                    id = "", // Will be set by backend
                    firstName = _firstName.value.trim(),
                    lastName = _lastName.value.trim(),
                    email = _email.value.trim(),
                    phone = _phone.value.trim().ifBlank { null },
                    address = _address.value.trim().ifBlank { null },
                    city = _city.value.trim().ifBlank { null },
                    state = _state.value.trim().ifBlank { null },
                    zipCode = _zipCode.value.trim().ifBlank { null },
                    country = _country.value.trim().ifBlank { null },
                    totalOrders = 0,
                    totalSpent = 0.0,
                    createdAt = null,
                    lastOrderDate = null
                )

                val response = customerRepository.createCustomer(customer)

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = CustomerCreateUiState.Success(response.body()!!)
                } else {
                    _uiState.value = CustomerCreateUiState.Error(
                        response.message() ?: "Failed to create customer"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CustomerCreateUiState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun clearError() {
        if (_uiState.value is CustomerCreateUiState.Error) {
            _uiState.value = CustomerCreateUiState.Idle
        }
    }
}

sealed class CustomerCreateUiState {
    object Idle : CustomerCreateUiState()
    object Loading : CustomerCreateUiState()
    data class Success(val customer: Customer) : CustomerCreateUiState()
    data class Error(val message: String) : CustomerCreateUiState()
}
