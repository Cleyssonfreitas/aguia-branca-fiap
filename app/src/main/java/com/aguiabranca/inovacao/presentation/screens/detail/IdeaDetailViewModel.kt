package com.aguiabranca.inovacao.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.ReviewIdeaRequest
import com.aguiabranca.inovacao.domain.repository.IdeaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IdeaDetailUiState(
    val isLoading: Boolean = false,
    val message: String? = null
)

class IdeaDetailViewModel(
    private val ideaRepository: IdeaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdeaDetailUiState())
    val uiState: StateFlow<IdeaDetailUiState> = _uiState

    fun prioritizeIdea(id: String, priority: Int) {
        viewModelScope.launch {
            handleMutation(ideaRepository.reviewIdea(ReviewIdeaRequest(ideaId = id, priority = priority)), "Prioridade atualizada.")
        }
    }

    fun approveIdea(id: String) {
        viewModelScope.launch {
            handleMutation(ideaRepository.reviewIdea(ReviewIdeaRequest(ideaId = id, approved = true)), "Ideia aprovada.")
        }
    }

    fun rejectIdea(id: String, reason: String) {
        viewModelScope.launch {
            handleMutation(ideaRepository.reviewIdea(ReviewIdeaRequest(ideaId = id, approved = false, rejectionReason = reason)), "Ideia rejeitada.")
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
            return IdeaDetailViewModel(ideaRepository = container.ideaRepository) as T
        }
    }
}
