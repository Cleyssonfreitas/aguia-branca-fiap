package com.aguiabranca.inovacao.data.repository

import com.aguiabranca.inovacao.data.local.dao.ProjectDao
import com.aguiabranca.inovacao.data.local.toEntity
import com.aguiabranca.inovacao.data.local.toModel
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.SaveProjectRequest
import com.aguiabranca.inovacao.domain.repository.ProjectRepository
import com.aguiabranca.inovacao.domain.models.Idea
import com.aguiabranca.inovacao.domain.models.IdeaStatus
import com.aguiabranca.inovacao.domain.models.Project
import com.aguiabranca.inovacao.domain.models.User
import com.aguiabranca.inovacao.domain.models.UserRole
import com.aguiabranca.inovacao.domain.models.toUserRoleOrDefault
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseProjectRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDb: FirebaseDatabase,
    private val projectDao: ProjectDao
) : ProjectRepository {
    private val projectsRef = firebaseDb.reference.child("projects")
    private val ideasRef = firebaseDb.reference.child("ideas")
    private val usersRef = firebaseDb.reference.child("users")

    override suspend fun listProjects(): AppResult<List<Project>> {
        return try {
            requireAnyRole(UserRole.GESTOR, UserRole.LIDERANCA, UserRole.ADMIN_TI)
            val projects = projectsRef.get().await().children.mapNotNull { it.getValue(Project::class.java) }
                .sortedByDescending { it.updatedAt }
            projectDao.upsertAll(projects.map { it.toEntity() })
            AppResult.Success(projects)
        } catch (e: Exception) {
            val cached = projectDao.getAll().map { it.toModel() }
            if (cached.isNotEmpty()) AppResult.Success(cached) else AppResult.Error(e.message ?: "Não foi possível carregar projetos.", e)
        }
    }

    override suspend fun saveProject(request: SaveProjectRequest): AppResult<Project> {
        return try {
            val user = requireAnyRole(UserRole.GESTOR, UserRole.LIDERANCA)
            request.relatedIdeas.forEach { ideaId ->
                val idea = ideasRef.child(ideaId).get().await().getValue(Idea::class.java)
                if (idea?.status != IdeaStatus.APROVADA.name) error("Projetos só podem vincular ideias aprovadas.")
            }
            val now = System.currentTimeMillis()
            val id = request.id.ifBlank { projectsRef.push().key.orEmpty() }
            val existing = if (request.id.isBlank()) null else projectsRef.child(request.id).get().await().getValue(Project::class.java)
            val project = Project(
                id = id,
                title = request.title,
                description = request.description,
                stage = request.stage,
                status = request.status,
                createdBy = existing?.createdBy ?: user.uid,
                owner = request.owner.ifBlank { user.uid },
                createdAt = existing?.createdAt ?: now,
                updatedAt = now,
                startDate = request.startDate,
                endDate = request.endDate,
                investment = request.investment,
                roi = request.roi,
                profit = request.profit,
                costReduction = request.costReduction,
                productivityGain = request.productivityGain,
                progress = request.progress.coerceIn(0, 100),
                relatedIdeas = request.relatedIdeas
            )
            projectsRef.child(id).setValue(project).await()
            projectDao.upsert(project.toEntity())
            AppResult.Success(project)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Não foi possível salvar projeto.", e)
        }
    }

    private suspend fun requireAnyRole(vararg roles: UserRole): User {
        val uid = firebaseAuth.currentUser?.uid ?: error("Usuário não autenticado.")
        val user = usersRef.child(uid).get().await().getValue(User::class.java) ?: error("Perfil não encontrado.")
        if (!user.isActive || !roles.contains(user.role.toUserRoleOrDefault())) error("Permissão insuficiente.")
        return user
    }
}

