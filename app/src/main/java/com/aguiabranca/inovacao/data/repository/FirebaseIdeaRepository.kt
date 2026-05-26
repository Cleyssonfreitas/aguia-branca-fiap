package com.aguiabranca.inovacao.data.repository

import com.aguiabranca.inovacao.data.local.dao.IdeaDao
import com.aguiabranca.inovacao.data.local.dao.PendingActionDao
import com.aguiabranca.inovacao.data.local.entity.PendingActionEntity
import com.aguiabranca.inovacao.data.local.toEntity
import com.aguiabranca.inovacao.data.local.toModel
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.CreateIdeaRequest
import com.aguiabranca.inovacao.domain.ReviewIdeaRequest
import com.aguiabranca.inovacao.domain.repository.IdeaRepository
import com.aguiabranca.inovacao.domain.models.Idea
import com.aguiabranca.inovacao.domain.models.IdeaStatus
import com.aguiabranca.inovacao.domain.models.User
import com.aguiabranca.inovacao.domain.models.UserRole
import com.aguiabranca.inovacao.domain.models.toUserRoleOrDefault
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

class FirebaseIdeaRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDb: FirebaseDatabase,
    private val ideaDao: IdeaDao,
    private val pendingActionDao: PendingActionDao,
    private val gson: Gson
) : IdeaRepository {
    private val ideasRef = firebaseDb.reference.child("ideas")
    private val usersRef = firebaseDb.reference.child("users")

    override suspend fun createIdea(request: CreateIdeaRequest): AppResult<Idea> {
        return try {
            val user = requireAnyRole(UserRole.OPERADOR)
            val id = ideasRef.push().key.orEmpty()
            val now = System.currentTimeMillis()
            val idea = Idea(
                id = id,
                title = request.title,
                description = request.description,
                type = request.type,
                status = IdeaStatus.SUBMETIDA.name,
                createdBy = user.uid,
                createdAt = now,
                updatedAt = now,
                estimatedImpact = request.estimatedImpact,
                department = request.department,
                aiScore = request.aiScore,
                aiFeedback = request.aiFeedback
            )
            ideasRef.child(id).setValue(idea).await()
            ideaDao.upsert(idea.toEntity())
            AppResult.Success(idea)
        } catch (e: Exception) {
            val uid = firebaseAuth.currentUser?.uid.orEmpty()
            val localIdea = Idea(
                id = "local_${System.currentTimeMillis()}",
                title = request.title,
                description = request.description,
                type = request.type,
                status = IdeaStatus.RASCUNHO.name,
                createdBy = uid,
                estimatedImpact = request.estimatedImpact,
                department = request.department
            )
            ideaDao.upsert(localIdea.toEntity())
            pendingActionDao.insert(PendingActionEntity(actionType = "CREATE_IDEA", pendingPayload = gson.toJson(request), lastError = e.message))
            AppResult.Success(localIdea)
        }
    }

    override suspend fun listMyIdeas(): AppResult<List<Idea>> {
        return try {
            val user = requireAnyRole(UserRole.OPERADOR)
            val ideas = ideasRef.orderByChild("createdBy").equalTo(user.uid).get().await()
                .children.mapNotNull { it.getValue(Idea::class.java) }
                .sortedByDescending { it.updatedAt }
            ideaDao.upsertAll(ideas.map { it.toEntity() })
            AppResult.Success(ideas)
        } catch (e: Exception) {
            val uid = firebaseAuth.currentUser?.uid.orEmpty()
            val cached = ideaDao.getByUser(uid).map { it.toModel() }
            if (cached.isNotEmpty()) AppResult.Success(cached) else AppResult.Error(e.message ?: "Não foi possível carregar ideias.", e)
        }
    }

    override suspend fun listIdeasForReview(): AppResult<List<Idea>> {
        return try {
            requireAnyRole(UserRole.GESTOR, UserRole.ADMIN_TI)
            val ideas = ideasRef.get().await().children.mapNotNull { it.getValue(Idea::class.java) }
                .sortedWith(compareByDescending<Idea> { it.priority }.thenByDescending { it.updatedAt })
            ideaDao.upsertAll(ideas.map { it.toEntity() })
            AppResult.Success(ideas)
        } catch (e: Exception) {
            val cached = ideaDao.getAll().map { it.toModel() }
            if (cached.isNotEmpty()) AppResult.Success(cached) else AppResult.Error(e.message ?: "Não foi possível carregar ideias para avaliação.", e)
        }
    }

    override suspend fun reviewIdea(request: ReviewIdeaRequest): AppResult<Idea> {
        return try {
            val user = requireAnyRole(UserRole.GESTOR)
            val current = ideasRef.child(request.ideaId).get().await().getValue(Idea::class.java)
                ?: error("Ideia não encontrada.")
            val reviewed = current.copy(
                priority = request.priority ?: current.priority,
                status = when (request.approved) {
                    true -> IdeaStatus.APROVADA.name
                    false -> IdeaStatus.REJEITADA.name
                    null -> current.status
                },
                approvedBy = if (request.approved == true) user.uid else current.approvedBy,
                rejectionReason = if (request.approved == false) request.rejectionReason else current.rejectionReason,
                updatedAt = System.currentTimeMillis()
            )
            ideasRef.child(request.ideaId).setValue(reviewed).await()
            ideaDao.upsert(reviewed.toEntity())
            AppResult.Success(reviewed)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Não foi possível avaliar ideia.", e)
        }
    }

    private suspend fun requireAnyRole(vararg roles: UserRole): User {
        val uid = firebaseAuth.currentUser?.uid ?: error("Usuário não autenticado.")
        val user = usersRef.child(uid).get().await().getValue(User::class.java) ?: error("Perfil não encontrado.")
        if (!user.isActive || !roles.contains(user.role.toUserRoleOrDefault())) error("Permissão insuficiente.")
        return user
    }
}

