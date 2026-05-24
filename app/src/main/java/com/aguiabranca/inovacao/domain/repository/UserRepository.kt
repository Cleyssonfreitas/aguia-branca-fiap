package com.aguiabranca.inovacao.domain.repository

import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.CreateUserRequest
import com.aguiabranca.inovacao.domain.SetUserActiveRequest
import com.aguiabranca.inovacao.domain.UpdateUserRoleRequest
import com.aguiabranca.inovacao.domain.models.User

interface UserRepository {
    suspend fun createUser(request: CreateUserRequest): AppResult<User>
    suspend fun listUsers(): AppResult<List<User>>
    suspend fun updateUserRole(request: UpdateUserRoleRequest): AppResult<User>
    suspend fun setUserActive(request: SetUserActiveRequest): AppResult<User>
}

