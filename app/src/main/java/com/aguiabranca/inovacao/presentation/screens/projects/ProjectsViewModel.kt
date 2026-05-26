package com.aguiabranca.inovacao.presentation.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.SaveProjectRequest
import com.aguiabranca.inovacao.domain.models.Idea
import com.aguiabranca.inovacao.domain.models.IdeaStatus
import com.aguiabranca.inovacao.domain.models.Project
import com.aguiabranca.inovacao.domain.repository.IdeaRepository
import com.aguiabranca.inovacao.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProjectsUiState(
    val isLoading: Boolean = false,
    val projects: List<Project> = emptyList(),
    val approvedIdeas: List<Idea> = emptyList(),
    val message: String? = null
)

class ProjectsViewModel(
    private val projectRepository: ProjectRepository,
    private val ideaRepository: IdeaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState

    init {
        loadProjects()
        loadApprovedIdeas()
    }

    fun loadProjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = projectRepository.listProjects()) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(projects = result.data, isLoading = false) }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(message = result.message, isLoading = false) }
                }
            }
        }
    }

    fun loadApprovedIdeas() {
        viewModelScope.launch {
            // We use listIdeasForReview to get all ideas, and filter approved ones
            when (val result = ideaRepository.listIdeasForReview()) {
                is AppResult.Success -> {
                    val approved = result.data.filter { it.status == IdeaStatus.APROVADA.name }
                    _uiState.update { it.copy(approvedIdeas = approved) }
                }
                is AppResult.Error -> {
                    // Fail silently for ideas list if project listing works
                }
            }
        }
    }

    fun createProject(
        title: String,
        description: String,
        stage: String,
        status: String,
        investment: Double,
        profit: Double,
        progress: Int,
        relatedIdeas: List<String>
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val request = SaveProjectRequest(
                id = "",
                title = title.trim(),
                description = description.trim(),
                stage = stage,
                status = status,
                owner = "",
                investment = investment,
                profit = profit,
                progress = progress,
                relatedIdeas = relatedIdeas
            )
            when (val result = projectRepository.saveProject(request)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(message = "Projeto criado com sucesso.", isLoading = false) }
                    loadProjects()
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(message = result.message, isLoading = false) }
                }
            }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProjectsViewModel(
                projectRepository = container.projectRepository,
                ideaRepository = container.ideaRepository
            ) as T
        }
    }
}
