package com.ecommercestarter.admin.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _emailUpdateState = MutableStateFlow<AccountActionState>(AccountActionState.Idle)
    val emailUpdateState: StateFlow<AccountActionState> = _emailUpdateState.asStateFlow()

    private val _passwordChangeState = MutableStateFlow<AccountActionState>(AccountActionState.Idle)
    val passwordChangeState: StateFlow<AccountActionState> = _passwordChangeState.asStateFlow()

    fun updateEmail(newEmail: String, currentPassword: String) {
        if (newEmail.isBlank()) {
            _emailUpdateState.value = AccountActionState.Error("Email cannot be empty")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            _emailUpdateState.value = AccountActionState.Error("Invalid email format")
            return
        }

        if (currentPassword.isBlank()) {
            _emailUpdateState.value = AccountActionState.Error("Password is required")
            return
        }

        _emailUpdateState.value = AccountActionState.Loading

        viewModelScope.launch {
            try {
                val response = accountRepository.updateEmail(newEmail, currentPassword)

                if (response.isSuccessful && response.body()?.success == true) {
                    _emailUpdateState.value = AccountActionState.Success(
                        response.body()!!.message
                    )
                } else {
                    _emailUpdateState.value = AccountActionState.Error(
                        response.body()?.message ?: "Failed to update email"
                    )
                }
            } catch (e: Exception) {
                _emailUpdateState.value = AccountActionState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        if (currentPassword.isBlank()) {
            _passwordChangeState.value = AccountActionState.Error("Current password is required")
            return
        }

        if (newPassword.isBlank()) {
            _passwordChangeState.value = AccountActionState.Error("New password is required")
            return
        }

        if (newPassword.length < 6) {
            _passwordChangeState.value = AccountActionState.Error("Password must be at least 6 characters")
            return
        }

        if (newPassword != confirmPassword) {
            _passwordChangeState.value = AccountActionState.Error("Passwords do not match")
            return
        }

        _passwordChangeState.value = AccountActionState.Loading

        viewModelScope.launch {
            try {
                val response = accountRepository.changePassword(
                    currentPassword,
                    newPassword,
                    confirmPassword
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    _passwordChangeState.value = AccountActionState.Success(
                        response.body()!!.message
                    )
                } else {
                    _passwordChangeState.value = AccountActionState.Error(
                        response.body()?.message ?: "Failed to change password"
                    )
                }
            } catch (e: Exception) {
                _passwordChangeState.value = AccountActionState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun clearEmailUpdateState() {
        _emailUpdateState.value = AccountActionState.Idle
    }

    fun clearPasswordChangeState() {
        _passwordChangeState.value = AccountActionState.Idle
    }
}

sealed class AccountActionState {
    object Idle : AccountActionState()
    object Loading : AccountActionState()
    data class Success(val message: String) : AccountActionState()
    data class Error(val message: String) : AccountActionState()
}
