package com.aguiabranca.inovacao.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.domain.usecase.PermissionUseCase
import com.aguiabranca.inovacao.domain.usecase.ProjectCalculationsUseCase
import com.aguiabranca.inovacao.domain.usecase.ValidationUseCase
import com.aguiabranca.inovacao.models.Project
import com.aguiabranca.inovacao.models.ProjectStatus
import com.aguiabranca.inovacao.models.User
import kotlinx.coroutines.launch

sealed class ProjectEvent {
    object Loading : ProjectEvent()
    data class SuccessCreate(val projectId: String) : ProjectEvent()
    data class SuccessUpdate(val projectId: String) : ProjectEvent()
    data class Error(val message: String) : ProjectEvent()
}

data class DashboardMetrics(
    val totalProjects: Int,
    val totalInvestment: Double,
    val totalReturn: Double,
    val totalROI: Double,
    val projectsInProgress: Int,
    val projectsCompleted: Int
)

class ProjectViewModel(
    private val currentUser: User?
) : ViewModel() {

    val projectEvent = MutableLiveData<ProjectEvent>()
    val allProjects = MutableLiveData<List<Project>>()
    val dashboardMetrics = MutableLiveData<DashboardMetrics>()

    fun createProject(
        title: String,
        description: String,
        investment: Double,
        expectedReturn: Double,
        deadline: String
    ) {
        viewModelScope.launch {
            projectEvent.value = ProjectEvent.Loading

            if (currentUser == null || !PermissionUseCase.canCreateProject(currentUser)) {
                projectEvent.value = ProjectEvent.Error("Apenas gestores podem criar projetos")
                return@launch
            }

            if (!ValidationUseCase.validateProjectCreation(title, description, investment, expectedReturn)) {
                projectEvent.value = ProjectEvent.Error("Preencha todos os campos corretamente")
                return@launch
            }

            val newProject = Project(
                title = title,
                description = description,
                stage = "Planejamento",
                status = ProjectStatus.PLANEJAMENTO.name,
                owner = currentUser.uid,
                investment = investment,
                roi = ProjectCalculationsUseCase.calculateROI(investment, expectedReturn),
                profit = expectedReturn
            )

            projectEvent.value = ProjectEvent.SuccessCreate(newProject.id.ifBlank { "new-project-id" })
        }
    }

    fun updateProjectStatus(projectId: String, newStatus: ProjectStatus) {
        viewModelScope.launch {
            if (currentUser == null || !PermissionUseCase.canEditProject(currentUser)) {
                projectEvent.value = ProjectEvent.Error("Voce nao tem permissao para atualizar projetos")
                return@launch
            }

            projectEvent.value = ProjectEvent.Loading
            projectEvent.value = ProjectEvent.SuccessUpdate(projectId)
        }
    }

    fun updateProjectResults(projectId: String, actualReturn: Double, actualInvestment: Double) {
        viewModelScope.launch {
            if (currentUser == null || !PermissionUseCase.canEditProject(currentUser)) {
                projectEvent.value = ProjectEvent.Error("Voce nao tem permissao para atualizar resultados")
                return@launch
            }

            projectEvent.value = ProjectEvent.Loading
            projectEvent.value = ProjectEvent.SuccessUpdate(projectId)
            calculateDashboard()
        }
    }

    fun calculateDashboard() {
        viewModelScope.launch {
            if (currentUser == null || !PermissionUseCase.canAccessDashboard(currentUser)) {
                projectEvent.value = ProjectEvent.Error("Voce nao tem acesso ao dashboard")
                return@launch
            }

            val projects = allProjects.value ?: emptyList()
            val totalProjects = projects.size
            val totalInvestment = projects.sumOf { it.investment }
            val totalReturn = projects.sumOf { it.profit }
            val totalROI = ProjectCalculationsUseCase.calculateTotalROI(totalReturn, totalInvestment)
            val projectsInProgress = projects.count { it.status == ProjectStatus.EM_PROGRESSO.name }
            val projectsCompleted = projects.count { it.status == ProjectStatus.CONCLUIDO.name }

            dashboardMetrics.value = DashboardMetrics(
                totalProjects = totalProjects,
                totalInvestment = totalInvestment,
                totalReturn = totalReturn,
                totalROI = totalROI,
                projectsInProgress = projectsInProgress,
                projectsCompleted = projectsCompleted
            )
        }
    }

    fun getProjectROI(project: Project): Double {
        return ProjectCalculationsUseCase.calculateROI(project.investment, project.profit)
    }
}
