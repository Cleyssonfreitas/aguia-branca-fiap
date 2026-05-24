package com.aguiabranca.inovacao.data.repository

import com.aguiabranca.inovacao.data.local.dao.UserDao
import com.aguiabranca.inovacao.data.local.toEntity
import com.aguiabranca.inovacao.data.local.toModel
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.CreateUserRequest
import com.aguiabranca.inovacao.domain.SetUserActiveRequest
import com.aguiabranca.inovacao.domain.UpdateUserRoleRequest
import com.aguiabranca.inovacao.domain.repository.UserRepository
import com.aguiabranca.inovacao.models.User
import com.aguiabranca.inovacao.models.UserRole
import com.aguiabranca.inovacao.models.toUserRoleOrDefault
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseUserRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDb: FirebaseDatabase,
    private val userDao: UserDao
) : UserRepository {
    private val usersRef = firebaseDb.reference.child("users")
    private val pendingUsersRef = firebaseDb.reference.child("pendingUsers")

    override suspend fun createUser(request: CreateUserRequest): AppResult<User> {
        return try {
            requireAdmin()
            val user = User(
                uid = "",
                email = request.email.trim().lowercase(),
                name = request.name.trim(),
                role = request.role.name,
                createdAt = System.currentTimeMillis(),
                isActive = true
            )
            pendingUsersRef.child(user.email.safeEmailKey()).setValue(user).await()
            AppResult.Success(user)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Não foi possível liberar usuário.", e)
        }
    }

    override suspend fun listUsers(): AppResult<List<User>> {
        return try {
            requireAdmin()
            val users = usersRef.get().await().children.mapNotNull { it.getValue(User::class.java) }
            val pendingUsers = pendingUsersRef.get().await().children.mapNotNull { it.getValue(User::class.java) }
            val allUsers = (users + pendingUsers).sortedWith(compareBy<User> { it.role }.thenBy { it.name })
            userDao.upsertAll(users.map { it.toEntity() })
            AppResult.Success(allUsers)
        } catch (e: Exception) {
            val cached = userDao.getAll().map { it.toModel() }
            if (cached.isNotEmpty()) AppResult.Success(cached) else AppResult.Error(e.message ?: "Não foi possível listar usuários.", e)
        }
    }

    override suspend fun updateUserRole(request: UpdateUserRoleRequest): AppResult<User> {
        return try {
            requireAdmin()
            val current = if (request.uid.isBlank()) {
                null
            } else {
                usersRef.child(request.uid).get().await().getValue(User::class.java)
            } ?: error("Usuário já criado precisa ter UID para alterar cargo.")

            val updated = current.copy(role = request.role.name)
            usersRef.child(request.uid).setValue(updated).await()
            userDao.upsert(updated.toEntity())
            AppResult.Success(updated)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Não foi possível atualizar cargo.", e)
        }
    }

    override suspend fun setUserActive(request: SetUserActiveRequest): AppResult<User> {
        return try {
            requireAdmin()
            val current = usersRef.child(request.uid).get().await().getValue(User::class.java)
                ?: error("Usuário não encontrado.")
            val updated = current.copy(isActive = request.isActive)
            usersRef.child(request.uid).setValue(updated).await()
            userDao.upsert(updated.toEntity())
            AppResult.Success(updated)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Não foi possível atualizar status.", e)
        }
    }

    private suspend fun requireAdmin() {
        val uid = firebaseAuth.currentUser?.uid ?: error("Usuário não autenticado.")
        val user = usersRef.child(uid).get().await().getValue(User::class.java)
            ?: error("Perfil não encontrado.")
        if (!user.isActive || user.role.toUserRoleOrDefault() != UserRole.ADMIN_TI) {
            error("Permissão insuficiente.")
        }
    }
}

