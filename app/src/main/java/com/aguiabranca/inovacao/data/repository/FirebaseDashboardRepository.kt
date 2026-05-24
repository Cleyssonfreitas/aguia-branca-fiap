package com.aguiabranca.inovacao.data.repository

import com.aguiabranca.inovacao.data.local.dao.DashboardDao
import com.aguiabranca.inovacao.data.local.toEntity
import com.aguiabranca.inovacao.data.local.toModel
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.DashboardSummary
import com.aguiabranca.inovacao.domain.repository.DashboardRepository
import com.aguiabranca.inovacao.models.Project
import com.aguiabranca.inovacao.models.ProjectStatus
import com.aguiabranca.inovacao.models.User
import com.aguiabranca.inovacao.models.UserRole
import com.aguiabranca.inovacao.models.toUserRoleOrDefault
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseDashboardRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDb: FirebaseDatabase,
    private val dashboardDao: DashboardDao
) : DashboardRepository {
    private val projectsRef = firebaseDb.reference.child("projects")
    private val usersRef = firebaseDb.reference.child("users")

    override suspend fun getLeadershipDashboard(): AppResult<DashboardSummary> {
        return try {
            requireAnyRole(UserRole.LIDERANCA, UserRole.ADMIN_TI)
            val projects = projectsRef.get().await().children.mapNotNull { it.getValue(Project::class.java) }
            val summary = calculate(projects)
            dashboardDao.upsert(summary.toEntity())
            AppResult.Success(summary)
        } catch (e: Exception) {
            val cached = dashboardDao.getLatest()?.toModel()
            if (cached != null) AppResult.Success(cached) else AppResult.Error(e.message ?: "Não foi possível carregar dashboard.", e)
        }
    }

    private fun calculate(projects: List<Project>): DashboardSummary {
        val count = projects.size
        return DashboardSummary(
            projectCount = count,
            completedProjects = projects.count { it.status == ProjectStatus.CONCLUIDO.name },
            totalInvestment = projects.sumOf { it.investment },
            totalProfit = projects.sumOf { it.profit },
            averageRoi = projects.takeIf { it.isNotEmpty() }?.map { it.roi }?.average() ?: 0.0,
            totalCostReduction = projects.sumOf { it.costReduction },
            averageProductivityGain = projects.takeIf { it.isNotEmpty() }?.map { it.productivityGain }?.average() ?: 0.0
        )
    }

    private suspend fun requireAnyRole(vararg roles: UserRole): User {
        val uid = firebaseAuth.currentUser?.uid ?: error("Usuário não autenticado.")
        val user = usersRef.child(uid).get().await().getValue(User::class.java) ?: error("Perfil não encontrado.")
        if (!user.isActive || !roles.contains(user.role.toUserRoleOrDefault())) error("Permissão insuficiente.")
        return user
    }
}

