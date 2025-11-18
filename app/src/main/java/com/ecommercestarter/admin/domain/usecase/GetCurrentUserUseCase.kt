package com.ecommercestarter.admin.domain.usecase

import com.ecommercestarter.admin.domain.model.User
import com.ecommercestarter.admin.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<User?> {
        return authRepository.getCurrentUser()
    }
}
