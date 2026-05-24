package com.aguiabranca.inovacao.data.repository

import com.aguiabranca.inovacao.data.local.dao.StrategyDao
import com.aguiabranca.inovacao.data.local.toEntity
import com.aguiabranca.inovacao.data.local.toModel
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.SaveStrategyRequest
import com.aguiabranca.inovacao.domain.repository.StrategyRepository
import com.aguiabranca.inovacao.models.Strategy
import com.aguiabranca.inovacao.models.User
import com.aguiabranca.inovacao.models.UserRole
import com.aguiabranca.inovacao.models.toUserRoleOrDefault
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseStrategyRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDb: FirebaseDatabase,
    private val strategyDao: StrategyDao
) : StrategyRepository {
    private val strategiesRef = firebaseDb.reference.child("strategies")
    private val usersRef = firebaseDb.reference.child("users")

    override suspend fun listStrategies(): AppResult<List<Strategy>> {
        return try {
            requireAnyRole(UserRole.LIDERANCA, UserRole.GESTOR, UserRole.OPERADOR, UserRole.ADMIN_TI)
            val strategies = strategiesRef.get().await().children.mapNotNull { it.getValue(Strategy::class.java) }
                .sortedWith(compareBy<Strategy> { it.priority }.thenByDescending { it.updatedAt })
            strategyDao.upsertAll(strategies.map { it.toEntity() })
            AppResult.Success(strategies)
        } catch (e: Exception) {
            val cached = strategyDao.getAll().map { it.toModel() }
            if (cached.isNotEmpty()) AppResult.Success(cached) else AppResult.Error(e.message ?: "Não foi possível carregar estratégias.", e)
        }
    }

    override suspend fun saveStrategy(request: SaveStrategyRequest): AppResult<Strategy> {
        return try {
            val user = requireAnyRole(UserRole.LIDERANCA)
            val now = System.currentTimeMillis()
            val id = request.id.ifBlank { strategiesRef.push().key.orEmpty() }
            val existing = if (request.id.isBlank()) null else strategiesRef.child(request.id).get().await().getValue(Strategy::class.java)
            val strategy = Strategy(
                id = id,
                title = request.title,
                description = request.description,
                createdBy = existing?.createdBy ?: user.uid,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now,
                isActive = request.isActive,
                priority = request.priority
            )
            strategiesRef.child(id).setValue(strategy).await()
            strategyDao.upsert(strategy.toEntity())
            AppResult.Success(strategy)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Não foi possível salvar estratégia.", e)
        }
    }

    override suspend fun deleteStrategy(strategyId: String): AppResult<Unit> {
        return try {
            requireAnyRole(UserRole.LIDERANCA)
            strategiesRef.child(strategyId).removeValue().await()
            strategyDao.deleteById(strategyId)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Não foi possível excluir estratégia.", e)
        }
    }

    private suspend fun requireAnyRole(vararg roles: UserRole): User {
        val uid = firebaseAuth.currentUser?.uid ?: error("Usuário não autenticado.")
        val user = usersRef.child(uid).get().await().getValue(User::class.java) ?: error("Perfil não encontrado.")
        if (!user.isActive || !roles.contains(user.role.toUserRoleOrDefault())) error("Permissão insuficiente.")
        return user
    }
}

