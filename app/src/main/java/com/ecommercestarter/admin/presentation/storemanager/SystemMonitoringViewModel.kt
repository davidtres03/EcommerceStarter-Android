package com.ecommercestarter.admin.presentation.storemanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.model.PerformanceMetrics
import com.ecommercestarter.admin.data.model.ServiceErrorDetail
import com.ecommercestarter.admin.data.model.ServiceStatus
import com.ecommercestarter.admin.data.repository.SystemMonitoringRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SystemMonitoringViewModel @Inject constructor(
    private val repository: SystemMonitoringRepository
) : ViewModel() {
    
    private val _statusState = MutableStateFlow<StatusState>(StatusState.Loading)
    val statusState: StateFlow<StatusState> = _statusState.asStateFlow()
    
    private val _errorsState = MutableStateFlow<ErrorsState>(ErrorsState.Loading)
    val errorsState: StateFlow<ErrorsState> = _errorsState.asStateFlow()
    
    private val _metricsState = MutableStateFlow<MetricsState>(MetricsState.Loading)
    val metricsState: StateFlow<MetricsState> = _metricsState.asStateFlow()
    
    private val _operationState = MutableStateFlow<MonitoringOperationState>(MonitoringOperationState.Idle)
    val operationState: StateFlow<MonitoringOperationState> = _operationState.asStateFlow()
    
    init {
        loadServiceStatus()
    }
    
    fun loadServiceStatus() {
        viewModelScope.launch {
            _statusState.value = StatusState.Loading
            repository.getServiceStatus().fold(
                onSuccess = { status ->
                    _statusState.value = StatusState.Success(status)
                },
                onFailure = { error ->
                    _statusState.value = StatusState.Error(error.message ?: "Failed to load service status")
                }
            )
        }
    }
    
    fun loadServiceErrors(
        severity: String? = null,
        source: String? = null,
        acknowledged: Boolean? = null,
        page: Int = 1
    ) {
        viewModelScope.launch {
            _errorsState.value = ErrorsState.Loading
            repository.getServiceErrors(severity, source, acknowledged, page).fold(
                onSuccess = { (errors, totalCount) ->
                    _errorsState.value = ErrorsState.Success(errors, totalCount)
                },
                onFailure = { error ->
                    _errorsState.value = ErrorsState.Error(error.message ?: "Failed to load service errors")
                }
            )
        }
    }
    
    fun loadPerformanceMetrics(
        startDate: String? = null,
        endDate: String? = null,
        groupBy: String = "hour"
    ) {
        viewModelScope.launch {
            _metricsState.value = MetricsState.Loading
            repository.getPerformanceMetrics(startDate, endDate, groupBy).fold(
                onSuccess = { metrics ->
                    _metricsState.value = MetricsState.Success(metrics)
                },
                onFailure = { error ->
                    _metricsState.value = MetricsState.Error(error.message ?: "Failed to load performance metrics")
                }
            )
        }
    }
    
    fun acknowledgeError(errorId: Int) {
        viewModelScope.launch {
            _operationState.value = MonitoringOperationState.Loading
            repository.acknowledgeError(errorId).fold(
                onSuccess = { message ->
                    _operationState.value = MonitoringOperationState.Success(message)
                    // Reload errors to reflect changes
                    loadServiceErrors()
                },
                onFailure = { error ->
                    _operationState.value = MonitoringOperationState.Error(error.message ?: "Failed to acknowledge error")
                }
            )
        }
    }
    
    fun resetOperationState() {
        _operationState.value = MonitoringOperationState.Idle
    }
}

sealed class StatusState {
    object Loading : StatusState()
    data class Success(val status: ServiceStatus) : StatusState()
    data class Error(val message: String) : StatusState()
}

sealed class ErrorsState {
    object Loading : ErrorsState()
    data class Success(val errors: List<ServiceErrorDetail>, val totalCount: Int) : ErrorsState()
    data class Error(val message: String) : ErrorsState()
}

sealed class MetricsState {
    object Loading : MetricsState()
    data class Success(val metrics: PerformanceMetrics) : MetricsState()
    data class Error(val message: String) : MetricsState()
}

sealed class MonitoringOperationState {
    object Idle : MonitoringOperationState()
    object Loading : MonitoringOperationState()
    data class Success(val message: String) : MonitoringOperationState()
    data class Error(val message: String) : MonitoringOperationState()
}
