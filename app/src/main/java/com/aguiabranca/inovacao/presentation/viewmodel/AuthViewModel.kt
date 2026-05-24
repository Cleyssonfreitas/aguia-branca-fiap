package com.aguiabranca.inovacao.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.LoginRequest
import com.aguiabranca.inovacao.domain.repository.AuthRepository
import com.aguiabranca.inovacao.domain.usecase.ValidationUseCase
import com.aguiabranca.inovacao.models.CurrentUser
import com.aguiabranca.inovacao.models.UserRole
import kotlinx.coroutines.launch

sealed class AuthEvent {
    object Loading : AuthEvent()
    data class Success(val user: CurrentUser) : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val authState = MutableLiveData<AuthEvent>()
    val currentUser = MutableLiveData<CurrentUser?>()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authState.value = AuthEvent.Loading

            if (!ValidationUseCase.validateEmail(email)) {
                authState.value = AuthEvent.Error("Email invalido")
                return@launch
            }

            if (!ValidationUseCase.validatePassword(password)) {
                authState.value = AuthEvent.Error("Senha deve ter pelo menos 6 caracteres")
                return@launch
            }

            when (val result = authRepository.signIn(LoginRequest(email, password))) {
                is AppResult.Success -> {
                    currentUser.value = result.data
                    authState.value = AuthEvent.Success(result.data)
                }
                is AppResult.Error -> authState.value = AuthEvent.Error(result.message)
            }
        }
    }

    fun signup(email: String, password: String, name: String, role: UserRole) {
        viewModelScope.launch {
            authState.value = AuthEvent.Loading

            if (!ValidationUseCase.validateEmail(email)) {
                authState.value = AuthEvent.Error("Email invalido")
                return@launch
            }

            if (!ValidationUseCase.validatePassword(password)) {
                authState.value = AuthEvent.Error("Senha deve ter pelo menos 6 caracteres")
                return@launch
            }

            if (!ValidationUseCase.validateName(name)) {
                authState.value = AuthEvent.Error("Nome invalido")
                return@launch
            }

            when (val result = authRepository.completeFirstAccess(LoginRequest(email, password))) {
                is AppResult.Success -> {
                    currentUser.value = result.data
                    authState.value = AuthEvent.Success(result.data)
                }
                is AppResult.Error -> authState.value = AuthEvent.Error(result.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
            currentUser.value = null
        }
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}
