package com.aguiabranca.inovacao.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.models.Idea
import com.aguiabranca.inovacao.models.IdeaStatus
import com.aguiabranca.inovacao.data.repository.IdeaRepository
import com.aguiabranca.inovacao.data.repository.AuthResult
import com.aguiabranca.inovacao.domain.usecase.PermissionUseCase
import com.aguiabranca.inovacao.domain.usecase.ValidationUseCase
import com.aguiabranca.inovacao.models.User
import kotlinx.coroutines.launch

sealed class IdeaEvent {
    object Loading : IdeaEvent()
    data class SuccessCreate(val ideaId: String) : IdeaEvent()
    data class SuccessApprove(val ideaId: String) : IdeaEvent()
    data class SuccessReject(val ideaId: String) : IdeaEvent()
    data class Error(val message: String) : IdeaEvent()
}

class IdeaViewModel(
    private val ideaRepository: IdeaRepository,
    private val currentUser: User?
) : ViewModel() {
    
    val ideaEvent = MutableLiveData<IdeaEvent>()
    val allIdeas = MutableLiveData<List<Idea>>()
    val userIdeas = MutableLiveData<List<Idea>>()

    init {
        loadAllIdeas()
    }

    fun createIdea(title: String, description: String, ideaType: String) {
        viewModelScope.launch {
            ideaEvent.value = IdeaEvent.Loading
            
            if (!ValidationUseCase.validateIdeaCreation(title, description)) {
                ideaEvent.value = IdeaEvent.Error("Preencha todos os campos corretamente")
                return@launch
            }
            
            if (currentUser == null) {
                ideaEvent.value = IdeaEvent.Error("Usuário não autenticado")
                return@launch
            }
            
            try {
                val newIdea = Idea(
                    id = "",
                    title = title,
                    description = description,
                    type = ideaType,
                    createdBy = currentUser.uid,
                    status = IdeaStatus.ENVIADA
                )
                
                val result = ideaRepository.createIdea(newIdea)
                when (result) {
                    is AuthResult.Success<String> -> {
                        ideaEvent.value = IdeaEvent.SuccessCreate(result.data)
                        loadAllIdeas()
                    }
                    is AuthResult.Error -> ideaEvent.value = IdeaEvent.Error(result.exception.message ?: "Erro ao criar ideia")
                    is AuthResult.Loading -> {}
                }
            } catch (e: Exception) {
                ideaEvent.value = IdeaEvent.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun approveIdea(ideaId: String) {
        viewModelScope.launch {
            if (currentUser == null || !PermissionUseCase.canApproveIdea(currentUser)) {
                ideaEvent.value = IdeaEvent.Error("Você não tem permissão para aprovar ideias")
                return@launch
            }
            
            ideaEvent.value = IdeaEvent.Loading
            
            try {
                val result = ideaRepository.approveIdea(ideaId, currentUser.uid)
                when (result) {
                    is AuthResult.Success -> {
                        ideaEvent.value = IdeaEvent.SuccessApprove(ideaId)
                        loadAllIdeas()
                    }
                    is AuthResult.Error -> ideaEvent.value = IdeaEvent.Error(result.exception.message ?: "Erro ao aprovar")
                    is AuthResult.Loading -> {}
                }
            } catch (e: Exception) {
                ideaEvent.value = IdeaEvent.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun rejectIdea(ideaId: String, reason: String) {
        viewModelScope.launch {
            if (currentUser == null || !PermissionUseCase.canApproveIdea(currentUser)) {
                ideaEvent.value = IdeaEvent.Error("Você não tem permissão para rejeitar ideias")
                return@launch
            }
            
            ideaEvent.value = IdeaEvent.Loading
            
            if (reason.isBlank()) {
                ideaEvent.value = IdeaEvent.Error("Motivo da rejeição é obrigatório")
                return@launch
            }
            
            try {
                val result = ideaRepository.rejectIdea(ideaId, reason)
                when (result) {
                    is AuthResult.Success -> {
                        ideaEvent.value = IdeaEvent.SuccessReject(ideaId)
                        loadAllIdeas()
                    }
                    is AuthResult.Error -> ideaEvent.value = IdeaEvent.Error(result.exception.message ?: "Erro ao rejeitar")
                    is AuthResult.Loading -> {}
                }
            } catch (e: Exception) {
                ideaEvent.value = IdeaEvent.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun loadAllIdeas() {
        viewModelScope.launch {
            try {
                val ideas = ideaRepository.getAllIdeasLiveData()
                allIdeas.value = ideas.value ?: emptyList()
            } catch (e: Exception) {
                ideaEvent.value = IdeaEvent.Error("Erro ao carregar ideias")
            }
        }
    }

    fun loadUserIdeas(userId: String) {
        viewModelScope.launch {
            try {
                val result = ideaRepository.getIdeasByUser(userId)
                when (result) {
                    is AuthResult.Success -> userIdeas.value = result.data
                    is AuthResult.Error -> ideaEvent.value = IdeaEvent.Error(result.exception.message ?: "Erro ao carregar")
                    is AuthResult.Loading -> {}
                }
            } catch (e: Exception) {
                ideaEvent.value = IdeaEvent.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
}

