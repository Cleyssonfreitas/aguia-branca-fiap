package com.aguiabranca.inovacao.domain.repository

import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.LoginRequest
import com.aguiabranca.inovacao.domain.models.CurrentUser

interface AuthRepository {
    suspend fun signIn(request: LoginRequest): AppResult<CurrentUser>
    suspend fun completeFirstAccess(request: LoginRequest): AppResult<CurrentUser>
    suspend fun signOut(): AppResult<Unit>
    suspend fun getCurrentUser(): AppResult<CurrentUser?>
    fun isUserLoggedIn(): Boolean
}
