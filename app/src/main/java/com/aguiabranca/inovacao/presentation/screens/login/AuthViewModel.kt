package com.aguiabranca.inovacao.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.LoginRequest
import com.aguiabranca.inovacao.domain.models.CurrentUser
import com.aguiabranca.inovacao.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.aguiabranca.inovacao.data.local.datastore.SessionManager

data class AuthUiState(
    val isLoading: Boolean = true,
    val currentUser: CurrentUser? = null,
    val message: String? = null
)


class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            when (val result = authRepository.getCurrentUser()) {
                is AppResult.Success -> {
                    val user = result.data
                    if (user != null) {
                        sessionManager.saveSession(user.uid, user.role.name, user.name)
                    }
                    _uiState.update { it.copy(currentUser = user, isLoading = false) }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, message = result.message) }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            val request = LoginRequest(email.trim(), password)
            val loginResult = authRepository.signIn(request)
            val result = if (loginResult is AppResult.Error && (loginResult.message.contains("There is no user record", ignoreCase = true) || loginResult.message.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) || loginResult.message.contains("The supplied auth credential is incorrect", ignoreCase = true))) {
                val firstAccessResult = authRepository.completeFirstAccess(request)
                if (firstAccessResult is AppResult.Error && firstAccessResult.message.contains("already in use", ignoreCase = true)) {
                    loginResult // User already exists, so it was just a wrong password
                } else {
                    firstAccessResult
                }
            } else {
                loginResult
            }
            
            when (result) {
                is AppResult.Success -> {
                    val user = result.data
                    sessionManager.saveSession(user.uid, user.role.name, user.name)
                    _uiState.update { it.copy(currentUser = user, isLoading = false, message = "Login realizado.") }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, message = result.message) }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
            sessionManager.clearSession()
            _uiState.update { it.copy(currentUser = null, isLoading = false) }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun refreshUser() {
        checkSession()
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(
                authRepository = container.authRepository,
                sessionManager = container.sessionManager
            ) as T
        }
    }
}
