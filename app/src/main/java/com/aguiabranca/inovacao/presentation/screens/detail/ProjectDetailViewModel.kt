package com.aguiabranca.inovacao.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.SaveProjectRequest
import com.aguiabranca.inovacao.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProjectDetailUiState(
    val isLoading: Boolean = false,
    val message: String? = null
)

class ProjectDetailViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState

    fun updateProject(id: String, title: String, description: String, stage: String, status: String, investment: Double, profit: Double, progress: Int) {
        viewModelScope.launch {
            val roi = if (investment > 0) ((profit - investment) / investment) * 100 else 0.0
            
            // Reusing SaveProjectRequest assuming the backend handles updates identically or repository manages it
            handleMutation(
                projectRepository.saveProject(
                    SaveProjectRequest(
                        id = id,
                        title = title.trim(),
                        description = description.trim(),
                        stage = stage,
                        status = status,
                        owner = "", // Ideally fetched from current project or user
                        investment = investment,
                        profit = profit,
                        roi = roi,
                        progress = progress
                    )
                ),
                "Projeto atualizado."
            )
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun <T> handleMutation(result: AppResult<T>, successMessage: String) {
        when (result) {
            is AppResult.Success -> {
                _uiState.update { it.copy(message = successMessage) }
            }
            is AppResult.Error -> _uiState.update { it.copy(message = result.message) }
        }
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProjectDetailViewModel(projectRepository = container.projectRepository) as T
        }
    }
}
