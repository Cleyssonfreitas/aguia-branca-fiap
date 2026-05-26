package com.aguiabranca.inovacao.presentation.screens.orientations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.SaveStrategyRequest
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

    fun saveStrategy(title: String, description: String, priority: Int, id: String = "") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val request = SaveStrategyRequest(
                id = id,
                title = title.trim(),
                description = description.trim(),
                priority = priority,
                isActive = true
            )
            when (val result = strategyRepository.saveStrategy(request)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(message = "Estratégia salva com sucesso.", isLoading = false) }
                    loadStrategies()
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(message = result.message, isLoading = false) }
                }
            }
        }
    }

    fun deleteStrategy(strategyId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = strategyRepository.deleteStrategy(strategyId)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(message = "Estratégia excluída.", isLoading = false) }
                    loadStrategies()
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
