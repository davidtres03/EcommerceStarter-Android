package com.ecommercestarter.admin.presentation.storemanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.model.SecuritySettings
import com.ecommercestarter.admin.data.repository.SecurityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Security Settings screen
 */
@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val repository: SecurityRepository
) : ViewModel() {
    
    private val _settingsState = MutableStateFlow<SettingsState>(SettingsState.Loading)
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    /**
     * Load security settings
     */
    fun loadSettings() {
        viewModelScope.launch {
            _settingsState.value = SettingsState.Loading
            
            repository.getSecuritySettings()
                .onSuccess { settings ->
                    _settingsState.value = SettingsState.Success(settings)
                }
                .onFailure { error ->
                    _settingsState.value = SettingsState.Error(
                        error.message ?: "Failed to load security settings"
                    )
                }
        }
    }
    
    sealed class SettingsState {
        object Loading : SettingsState()
        data class Success(val settings: SecuritySettings) : SettingsState()
        data class Error(val message: String) : SettingsState()
    }
}
