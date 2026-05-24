package com.aguiabranca.inovacao.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.CreateIdeaRequest
import com.aguiabranca.inovacao.domain.ReviewIdeaRequest
import com.aguiabranca.inovacao.domain.repository.IdeaRepository
import com.aguiabranca.inovacao.domain.usecase.PermissionUseCase
import com.aguiabranca.inovacao.domain.usecase.ValidationUseCase
import com.aguiabranca.inovacao.models.Idea
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
                ideaEvent.value = IdeaEvent.Error("Usuario nao autenticado")
                return@launch
            }

            val request = CreateIdeaRequest(
                title = title,
                description = description,
                type = ideaType,
                estimatedImpact = "",
                department = ""
            )

            when (val result = ideaRepository.createIdea(request)) {
                is AppResult.Success -> {
                    ideaEvent.value = IdeaEvent.SuccessCreate(result.data.id)
                    loadAllIdeas()
                }
                is AppResult.Error -> ideaEvent.value = IdeaEvent.Error(result.message)
            }
        }
    }

    fun approveIdea(ideaId: String) {
        viewModelScope.launch {
            if (currentUser == null || !PermissionUseCase.canApproveIdea(currentUser)) {
                ideaEvent.value = IdeaEvent.Error("Voce nao tem permissao para aprovar ideias")
                return@launch
            }

            ideaEvent.value = IdeaEvent.Loading
            when (val result = ideaRepository.reviewIdea(ReviewIdeaRequest(ideaId = ideaId, approved = true))) {
                is AppResult.Success -> {
                    ideaEvent.value = IdeaEvent.SuccessApprove(ideaId)
                    loadAllIdeas()
                }
                is AppResult.Error -> ideaEvent.value = IdeaEvent.Error(result.message)
            }
        }
    }

    fun rejectIdea(ideaId: String, reason: String) {
        viewModelScope.launch {
            if (currentUser == null || !PermissionUseCase.canApproveIdea(currentUser)) {
                ideaEvent.value = IdeaEvent.Error("Voce nao tem permissao para rejeitar ideias")
                return@launch
            }

            ideaEvent.value = IdeaEvent.Loading

            if (reason.isBlank()) {
                ideaEvent.value = IdeaEvent.Error("Motivo da rejeicao e obrigatorio")
                return@launch
            }

            when (val result = ideaRepository.reviewIdea(ReviewIdeaRequest(ideaId = ideaId, approved = false, rejectionReason = reason))) {
                is AppResult.Success -> {
                    ideaEvent.value = IdeaEvent.SuccessReject(ideaId)
                    loadAllIdeas()
                }
                is AppResult.Error -> ideaEvent.value = IdeaEvent.Error(result.message)
            }
        }
    }

    fun loadAllIdeas() {
        viewModelScope.launch {
            val result = if (PermissionUseCase.canApproveIdea(currentUser)) {
                ideaRepository.listIdeasForReview()
            } else {
                ideaRepository.listMyIdeas()
            }

            when (result) {
                is AppResult.Success -> allIdeas.value = result.data
                is AppResult.Error -> ideaEvent.value = IdeaEvent.Error(result.message)
            }
        }
    }

    fun loadUserIdeas(userId: String) {
        viewModelScope.launch {
            when (val result = ideaRepository.listMyIdeas()) {
                is AppResult.Success -> userIdeas.value = result.data
                is AppResult.Error -> ideaEvent.value = IdeaEvent.Error(result.message)
            }
        }
    }
}
