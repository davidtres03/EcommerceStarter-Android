package com.ecommercestarter.admin.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.domain.model.AuthState
import com.ecommercestarter.admin.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()
    
    private val _rememberMe = MutableStateFlow(false)
    val rememberMe: StateFlow<Boolean> = _rememberMe.asStateFlow()
    
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }
    
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }
    
    fun onRememberMeChange(newValue: Boolean) {
        _rememberMe.value = newValue
    }
    
    fun login() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = loginUseCase(
                email = _email.value,
                password = _password.value,
                rememberMe = _rememberMe.value
            )
            
            _authState.value = result.fold(
                onSuccess = { user -> AuthState.Authenticated(user) },
                onFailure = { error -> AuthState.Error(error.message ?: "Unknown error") }
            )
        }
    }
    
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
}
