package com.ecommercestarter.admin.presentation.storemanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.model.ApiConfigGroup
import com.ecommercestarter.admin.data.repository.ApiConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for API Configuration Management screen
 */
@HiltViewModel
class ApiConfigViewModel @Inject constructor(
    private val repository: ApiConfigRepository
) : ViewModel() {
    
    private val _configState = MutableStateFlow<ConfigState>(ConfigState.Loading)
    val configState: StateFlow<ConfigState> = _configState.asStateFlow()
    
    private val _toggleState = MutableStateFlow<ToggleState>(ToggleState.Idle)
    val toggleState: StateFlow<ToggleState> = _toggleState.asStateFlow()
    
    init {
        loadConfigurations()
    }
    
    /**
     * Load all API configurations
     */
    fun loadConfigurations() {
        viewModelScope.launch {
            _configState.value = ConfigState.Loading
            
            repository.getApiConfigurations()
                .onSuccess { configGroups ->
                    _configState.value = ConfigState.Success(configGroups)
                }
                .onFailure { error ->
                    _configState.value = ConfigState.Error(
                        error.message ?: "Failed to load API configurations"
                    )
                }
        }
    }
    
    /**
     * Toggle an API configuration active/inactive
     */
    fun toggleConfiguration(configId: Int) {
        viewModelScope.launch {
            _toggleState.value = ToggleState.Loading(configId)
            
            repository.toggleApiConfiguration(configId)
                .onSuccess { newState ->
                    _toggleState.value = ToggleState.Success(configId, newState)
                    // Reload configurations to reflect the change
                    loadConfigurations()
                }
                .onFailure { error ->
                    _toggleState.value = ToggleState.Error(
                        configId,
                        error.message ?: "Failed to toggle configuration"
                    )
                }
        }
    }
    
    /**
     * Reset toggle state to idle
     */
    fun resetToggleState() {
        _toggleState.value = ToggleState.Idle
    }
    
    sealed class ConfigState {
        object Loading : ConfigState()
        data class Success(val configGroups: List<ApiConfigGroup>) : ConfigState()
        data class Error(val message: String) : ConfigState()
    }
    
    sealed class ToggleState {
        object Idle : ToggleState()
        data class Loading(val configId: Int) : ToggleState()
        data class Success(val configId: Int, val newState: Boolean) : ToggleState()
        data class Error(val configId: Int, val message: String) : ToggleState()
    }
}
