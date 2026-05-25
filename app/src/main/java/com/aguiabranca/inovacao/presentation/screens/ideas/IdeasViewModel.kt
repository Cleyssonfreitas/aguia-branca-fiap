package com.aguiabranca.inovacao.presentation.screens.ideas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.CreateIdeaRequest
import com.aguiabranca.inovacao.domain.models.Idea
import com.aguiabranca.inovacao.domain.models.IdeaType
import com.aguiabranca.inovacao.domain.models.UserRole
import com.aguiabranca.inovacao.domain.repository.IdeaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IdeasUiState(
    val isLoading: Boolean = false,
    val ideas: List<Idea> = emptyList(),
    val message: String? = null
)

class IdeasViewModel(
    private val ideaRepository: IdeaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdeasUiState())
    val uiState: StateFlow<IdeasUiState> = _uiState

    fun loadIdeas(role: UserRole) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = if (role == UserRole.OPERADOR) {
                ideaRepository.listMyIdeas()
            } else {
                ideaRepository.listIdeasForReview()
            }
            
            when (result) {
                is AppResult.Success -> _uiState.update { it.copy(ideas = result.data, isLoading = false) }
                is AppResult.Error -> _uiState.update { it.copy(message = result.message, isLoading = false) }
            }
        }
    }

    fun createIdea(title: String, description: String, impact: String, department: String, role: UserRole) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val request = CreateIdeaRequest(
                title = title.trim(),
                description = description.trim(),
                type = IdeaType.PROBLEMA.name,
                estimatedImpact = impact.trim(),
                department = department.trim()
            )
            when (val result = ideaRepository.createIdea(request)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(message = "Ideia registrada.", isLoading = false) }
                    loadIdeas(role)
                }
                is AppResult.Error -> _uiState.update { it.copy(message = result.message, isLoading = false) }
            }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IdeasViewModel(ideaRepository = container.ideaRepository) as T
        }
    }
}
