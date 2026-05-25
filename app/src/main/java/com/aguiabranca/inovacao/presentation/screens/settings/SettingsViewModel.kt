package com.aguiabranca.inovacao.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.data.local.datastore.SessionManager
import com.aguiabranca.inovacao.di.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    val themeState: StateFlow<Int> = sessionManager.themeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun setTheme(theme: Int) {
        viewModelScope.launch {
            sessionManager.saveTheme(theme)
        }
    }

    class Factory(private val appContainer: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(sessionManager = appContainer.sessionManager) as T
        }
    }
}
