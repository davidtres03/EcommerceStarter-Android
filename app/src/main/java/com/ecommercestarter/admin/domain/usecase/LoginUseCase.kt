package com.ecommercestarter.admin.domain.usecase

import com.ecommercestarter.admin.domain.model.User
import com.ecommercestarter.admin.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        rememberMe: Boolean = false
    ): Result<User> {
        // Add validation
        if (email.isBlank()) {
            return Result.failure(Exception("Email cannot be empty"))
        }
        if (password.isBlank()) {
            return Result.failure(Exception("Password cannot be empty"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email format"))
        }
        
        return authRepository.login(email, password, rememberMe)
    }
}
