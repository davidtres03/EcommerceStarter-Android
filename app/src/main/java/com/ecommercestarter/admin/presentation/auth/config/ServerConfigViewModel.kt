package com.ecommercestarter.admin.presentation.auth.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.preferences.UserPreferences
import com.ecommercestarter.admin.data.repository.BrandingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import java.net.URL
import javax.inject.Inject

sealed class ServerConfigState {
    object Initial : ServerConfigState()
    object Loading : ServerConfigState()
    object LoadingBranding : ServerConfigState()
    object Success : ServerConfigState()
    data class Error(val message: String) : ServerConfigState()
}

@HiltViewModel
class ServerConfigViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val brandingRepository: BrandingRepository
) : ViewModel() {

    private val _serverUrl = MutableStateFlow("")
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _configState = MutableStateFlow<ServerConfigState>(ServerConfigState.Initial)
    val configState: StateFlow<ServerConfigState> = _configState.asStateFlow()

    init {
        loadSavedServerUrl()
    }

    private fun loadSavedServerUrl() {
        viewModelScope.launch {
            userPreferences.serverUrl.collect { url ->
                if (!url.isNullOrEmpty()) {
                    _serverUrl.value = url
                }
            }
        }
    }

    fun onServerUrlChange(newUrl: String) {
        _serverUrl.value = newUrl
    }

    fun saveServerUrl() {
        viewModelScope.launch {
            _configState.value = ServerConfigState.Loading

            val url = _serverUrl.value.trim()

            if (url.isEmpty()) {
                _configState.value = ServerConfigState.Error("Server URL cannot be empty")
                return@launch
            }

            val validationResult = validateUrl(url)
            if (!validationResult.first) {
                _configState.value = ServerConfigState.Error(validationResult.second)
                return@launch
            }

            try {
                val normalizedUrl = normalizeUrl(url)
                Log.d("ServerConfig", "Saving server URL: $normalizedUrl")
                userPreferences.saveServerUrl(normalizedUrl)
                
                // Fetch branding configuration from server
                _configState.value = ServerConfigState.LoadingBranding
                Log.d("ServerConfig", "Fetching branding from server...")
                val brandingResult = brandingRepository.fetchAndCacheBranding()
                
                if (brandingResult.isSuccess) {
                    Log.d("ServerConfig", "Branding fetched successfully: ${brandingResult.getOrNull()}")
                    _configState.value = ServerConfigState.Success
                } else {
                    val error = brandingResult.exceptionOrNull()
                    Log.e("ServerConfig", "Failed to fetch branding: ${error?.message}", error)
                    // Still succeed even if branding fetch fails (will use defaults)
                    _configState.value = ServerConfigState.Success
                }
            } catch (e: Exception) {
                Log.e("ServerConfig", "Error saving server URL: ${e.message}", e)
                _configState.value = ServerConfigState.Error("Failed to save: ${e.message}")
            }
        }
    }

    private fun validateUrl(url: String): Pair<Boolean, String> {
        return try {
            val urlObj = URL(url)

            if (urlObj.protocol != "http" && urlObj.protocol != "https") {
                return false to "URL must start with http:// or https://"
            }

            if (urlObj.host.isEmpty()) {
                return false to "Invalid URL format"
            }

            true to ""
        } catch (e: Exception) {
            false to "Invalid URL format: ${e.message}"
        }
    }

    private fun normalizeUrl(url: String): String {
        var normalized = url.trim()
        if (!normalized.endsWith("/")) {
            normalized += "/"
        }
        return normalized
    }

    fun clearError() {
        if (_configState.value is ServerConfigState.Error) {
            _configState.value = ServerConfigState.Initial
        }
    }
}
