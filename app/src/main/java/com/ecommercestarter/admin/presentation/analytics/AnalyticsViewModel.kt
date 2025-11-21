package com.ecommercestarter.admin.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.model.AnalyticsData
import com.ecommercestarter.admin.data.repository.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State for Analytics screen
 */
sealed class AnalyticsState {
    object Loading : AnalyticsState()
    data class Success(val data: AnalyticsData) : AnalyticsState()
    data class Error(val message: String) : AnalyticsState()
}

/**
 * ViewModel for Analytics Dashboard screen
 */
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: AnalyticsRepository
) : ViewModel() {
    
    private val _analyticsState = MutableStateFlow<AnalyticsState>(AnalyticsState.Loading)
    val analyticsState: StateFlow<AnalyticsState> = _analyticsState.asStateFlow()
    
    init {
        loadAnalytics()
    }
    
    /**
     * Load analytics summary (last 30 days by default)
     */
    fun loadAnalytics() {
        viewModelScope.launch {
            _analyticsState.value = AnalyticsState.Loading
            
            repository.getAnalyticsSummary()
                .onSuccess { data ->
                    _analyticsState.value = AnalyticsState.Success(data)
                }
                .onFailure { error ->
                    _analyticsState.value = AnalyticsState.Error(
                        error.message ?: "Failed to load analytics"
                    )
                }
        }
    }
}
