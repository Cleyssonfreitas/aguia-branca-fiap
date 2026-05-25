package com.aguiabranca.inovacao.presentation.screens.orientations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.models.Strategy
import com.aguiabranca.inovacao.domain.repository.StrategyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrientationsUiState(
    val isLoading: Boolean = false,
    val strategies: List<Strategy> = emptyList(),
    val message: String? = null
)

class OrientationsViewModel(
    private val strategyRepository: StrategyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrientationsUiState())
    val uiState: StateFlow<OrientationsUiState> = _uiState

    init {
        loadStrategies()
    }

    fun loadStrategies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = strategyRepository.listStrategies()) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(strategies = result.data, isLoading = false) }
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
            return OrientationsViewModel(
                strategyRepository = container.strategyRepository
            ) as T
        }
    }
}
