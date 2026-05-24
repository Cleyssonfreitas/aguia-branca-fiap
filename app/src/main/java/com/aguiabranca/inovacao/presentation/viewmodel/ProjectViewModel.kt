package com.aguiabranca.inovacao.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.models.Project
import com.aguiabranca.inovacao.models.ProjectStatus
import com.aguiabranca.inovacao.models.User
import com.aguiabranca.inovacao.domain.usecase.PermissionUseCase
import com.aguiabranca.inovacao.domain.usecase.ProjectCalculationsUseCase
import com.aguiabranca.inovacao.domain.usecase.ValidationUseCase
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
            
            try {
                val newProject = Project(
                    id = "",
                    title = title,
                    description = description,
                    investment = investment,
                    expectedReturn = expectedReturn,
                    createdBy = currentUser.uid,
                    status = ProjectStatus.PLANEJAMENTO,
                    deadline = deadline
                )
                
                projectEvent.value = ProjectEvent.SuccessCreate("new-project-id")
            } catch (e: Exception) {
                projectEvent.value = ProjectEvent.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun updateProjectStatus(projectId: String, newStatus: ProjectStatus) {
        viewModelScope.launch {
            if (currentUser == null || !PermissionUseCase.canEditProject(currentUser)) {
                projectEvent.value = ProjectEvent.Error("Você não tem permissão para atualizar projetos")
                return@launch
            }
            
            projectEvent.value = ProjectEvent.Loading
            
            try {
                projectEvent.value = ProjectEvent.SuccessUpdate(projectId)
            } catch (e: Exception) {
                projectEvent.value = ProjectEvent.Error(e.message ?: "Erro ao atualizar")
            }
        }
    }

    fun updateProjectResults(projectId: String, actualReturn: Double, actualInvestment: Double) {
        viewModelScope.launch {
            if (currentUser == null || !PermissionUseCase.canEditProject(currentUser)) {
                projectEvent.value = ProjectEvent.Error("Você não tem permissão para atualizar resultados")
                return@launch
            }
            
            projectEvent.value = ProjectEvent.Loading
            
            try {
                projectEvent.value = ProjectEvent.SuccessUpdate(projectId)
                calculateDashboard()
            } catch (e: Exception) {
                projectEvent.value = ProjectEvent.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun calculateDashboard() {
        viewModelScope.launch {
            if (currentUser == null || !PermissionUseCase.canAccessDashboard(currentUser)) {
                projectEvent.value = ProjectEvent.Error("Você não tem acesso ao dashboard")
                return@launch
            }
            
            try {
                val projects = allProjects.value ?: emptyList()
                
                val totalProjects = projects.size
                val totalInvestment = projects.sumOf { it.investment }
                val totalReturn = projects.sumOf { it.expectedReturn }
                val totalROI = ProjectCalculationsUseCase.calculateTotalROI(totalReturn, totalInvestment)
                val projectsInProgress = projects.count { it.status == ProjectStatus.EXECUCAO }
                val projectsCompleted = projects.count { it.status == ProjectStatus.CONCLUIDO }
                
                dashboardMetrics.value = DashboardMetrics(
                    totalProjects = totalProjects,
                    totalInvestment = totalInvestment,
                    totalReturn = totalReturn,
                    totalROI = totalROI,
                    projectsInProgress = projectsInProgress,
                    projectsCompleted = projectsCompleted
                )
            } catch (e: Exception) {
                projectEvent.value = ProjectEvent.Error(e.message ?: "Erro ao calcular dashboard")
            }
        }
    }

    fun getProjectROI(project: Project): Double {
        return ProjectCalculationsUseCase.calculateROI(project.investment, project.expectedReturn)
    }
}

