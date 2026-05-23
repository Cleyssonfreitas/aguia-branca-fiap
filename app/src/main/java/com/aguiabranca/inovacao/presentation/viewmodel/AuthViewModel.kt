package com.aguiabranca.inovacao.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.models.User
import com.aguiabranca.inovacao.models.UserRole
import com.aguiabranca.inovacao.data.repository.AuthRepository
import com.aguiabranca.inovacao.data.repository.AuthResult
import com.aguiabranca.inovacao.domain.usecase.ValidationUseCase
import kotlinx.coroutines.launch

sealed class AuthEvent {
    object Loading : AuthEvent()
    data class Success(val user: User) : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    
    val authState = MutableLiveData<AuthEvent>()
    val currentUser = MutableLiveData<User?>()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authState.value = AuthEvent.Loading
            
            if (!ValidationUseCase.validateEmail(email)) {
                authState.value = AuthEvent.Error("Email inválido")
                return@launch
            }
            
            if (!ValidationUseCase.validatePassword(password)) {
                authState.value = AuthEvent.Error("Senha deve ter pelo menos 6 caracteres")
                return@launch
            }
            
            try {
                val result = authRepository.signIn(email, password)
                when (result) {
                    is AuthResult.Success -> {
                        currentUser.value = result.data
                        authState.value = AuthEvent.Success(result.data)
                    }
                    is AuthResult.Error -> {
                        authState.value = AuthEvent.Error(result.exception.message ?: "Erro desconhecido")
                    }
                    is AuthResult.Loading -> {}
                }
            } catch (e: Exception) {
                authState.value = AuthEvent.Error(e.message ?: "Erro na autenticação")
            }
        }
    }

    fun signup(email: String, password: String, name: String, role: UserRole) {
        viewModelScope.launch {
            authState.value = AuthEvent.Loading
            
            if (!ValidationUseCase.validateEmail(email)) {
                authState.value = AuthEvent.Error("Email inválido")
                return@launch
            }
            
            if (!ValidationUseCase.validatePassword(password)) {
                authState.value = AuthEvent.Error("Senha deve ter pelo menos 6 caracteres")
                return@launch
            }
            
            if (!ValidationUseCase.validateName(name)) {
                authState.value = AuthEvent.Error("Nome inválido")
                return@launch
            }
            
            try {
                val newUser = User(
                    uid = "",
                    email = email,
                    name = name,
                    role = role.name
                )
                
                val result = authRepository.signUp(email, password, name, role)
                when (result) {
                    is AuthResult.Success -> {
                        currentUser.value = result.data
                        authState.value = AuthEvent.Success(result.data)
                    }
                    is AuthResult.Error -> {
                        authState.value = AuthEvent.Error(result.exception.message ?: "Erro desconhecido")
                    }
                    is AuthResult.Loading -> {}
                }
            } catch (e: Exception) {
                authState.value = AuthEvent.Error(e.message ?: "Erro no cadastro")
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

