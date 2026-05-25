package com.aguiabranca.inovacao.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.CreateIdeaRequest
import com.aguiabranca.inovacao.domain.CreateUserRequest
import com.aguiabranca.inovacao.domain.DashboardSummary
import com.aguiabranca.inovacao.domain.SaveProjectRequest
import com.aguiabranca.inovacao.domain.SaveStrategyRequest
import com.aguiabranca.inovacao.domain.SetUserActiveRequest
import com.aguiabranca.inovacao.domain.UpdateUserRoleRequest
import com.aguiabranca.inovacao.domain.ReviewIdeaRequest
import com.aguiabranca.inovacao.domain.models.Idea
import com.aguiabranca.inovacao.domain.models.IdeaType
import com.aguiabranca.inovacao.domain.models.Project
import com.aguiabranca.inovacao.domain.models.ProjectStatus
import com.aguiabranca.inovacao.domain.models.Strategy
import com.aguiabranca.inovacao.domain.models.User
import com.aguiabranca.inovacao.domain.models.UserRole
import com.aguiabranca.inovacao.domain.repository.DashboardRepository
import com.aguiabranca.inovacao.domain.repository.IdeaRepository
import com.aguiabranca.inovacao.domain.repository.ProjectRepository
import com.aguiabranca.inovacao.domain.repository.StrategyRepository
import com.aguiabranca.inovacao.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val strategies: List<Strategy> = emptyList(),
    val myIdeas: List<Idea> = emptyList(),
    val reviewIdeas: List<Idea> = emptyList(),
    val projects: List<Project> = emptyList(),
    val dashboard: DashboardSummary = DashboardSummary(),
    val message: String? = null
)

class HomeViewModel(
    private val userRepository: UserRepository,
    private val strategyRepository: StrategyRepository,
    private val ideaRepository: IdeaRepository,
    private val projectRepository: ProjectRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun loadHomeData(role: UserRole) {
        viewModelScope.launch {
            loadStrategies()
            when (role) {
                UserRole.ADMIN_TI -> {
                    loadUsers()
                    loadProjects()
                    loadDashboard()
                }
                UserRole.LIDERANCA -> {
                    loadProjects()
                    loadDashboard()
                }
                UserRole.GESTOR -> {
                    loadIdeasForReview()
                    loadProjects()
                }
                UserRole.OPERADOR -> loadMyIdeas()
            }
        }
    }

    private suspend fun loadUsers() {
        when (val result = userRepository.listUsers()) {
            is AppResult.Success -> _uiState.update { it.copy(users = result.data) }
            is AppResult.Error -> _uiState.update { it.copy(message = result.message) }
        }
    }

    private suspend fun loadStrategies() {
        when (val result = strategyRepository.listStrategies()) {
            is AppResult.Success -> _uiState.update { it.copy(strategies = result.data) }
            is AppResult.Error -> _uiState.update { it.copy(message = result.message) }
        }
    }

    private suspend fun loadMyIdeas() {
        when (val result = ideaRepository.listMyIdeas()) {
            is AppResult.Success -> _uiState.update { it.copy(myIdeas = result.data) }
            is AppResult.Error -> _uiState.update { it.copy(message = result.message) }
        }
    }

    private suspend fun loadIdeasForReview() {
        when (val result = ideaRepository.listIdeasForReview()) {
            is AppResult.Success -> _uiState.update { it.copy(reviewIdeas = result.data) }
            is AppResult.Error -> _uiState.update { it.copy(message = result.message) }
        }
    }

    private suspend fun loadProjects() {
        when (val result = projectRepository.listProjects()) {
            is AppResult.Success -> _uiState.update { it.copy(projects = result.data) }
            is AppResult.Error -> _uiState.update { it.copy(message = result.message) }
        }
    }

    private suspend fun loadDashboard() {
        when (val result = dashboardRepository.getLeadershipDashboard()) {
            is AppResult.Success -> _uiState.update { it.copy(dashboard = result.data) }
            is AppResult.Error -> _uiState.update { it.copy(message = result.message) }
        }
    }

    fun createUser(name: String, email: String, password: String, role: UserRole) {
        viewModelScope.launch {
            handleMutation(
                result = userRepository.createUser(CreateUserRequest(name.trim(), email.trim(), password, role)),
                successMessage = "Usuário criado."
            ) { loadUsers() }
        }
    }

    fun updateUserRole(uid: String, role: UserRole) {
        viewModelScope.launch {
            handleMutation(userRepository.updateUserRole(UpdateUserRoleRequest(uid, role)), "Cargo atualizado.") { loadUsers() }
        }
    }

    fun setUserActive(uid: String, isActive: Boolean) {
        viewModelScope.launch {
            handleMutation(userRepository.setUserActive(SetUserActiveRequest(uid, isActive)), "Status atualizado.") { loadUsers() }
        }
    }

    fun saveStrategy(title: String, description: String, priority: Int) {
        viewModelScope.launch {
            handleMutation(
                strategyRepository.saveStrategy(SaveStrategyRequest(title = title.trim(), description = description.trim(), priority = priority)),
                "Orientação salva."
            ) { loadStrategies() }
        }
    }

    fun deleteStrategy(id: String) {
        viewModelScope.launch {
            handleMutation(strategyRepository.deleteStrategy(id), "Orientação excluída.") { loadStrategies() }
        }
    }

    fun createIdea(title: String, description: String, impact: String, department: String) {
        viewModelScope.launch {
            handleMutation(
                ideaRepository.createIdea(
                    CreateIdeaRequest(
                        title = title.trim(),
                        description = description.trim(),
                        type = IdeaType.PROBLEMA.name,
                        estimatedImpact = impact.trim(),
                        department = department.trim()
                    )
                ),
                "Ideia registrada."
            ) { loadMyIdeas() }
        }
    }

    fun prioritizeIdea(id: String, priority: Int) {
        viewModelScope.launch {
            handleMutation(ideaRepository.reviewIdea(ReviewIdeaRequest(ideaId = id, priority = priority)), "Prioridade atualizada.") { loadIdeasForReview() }
        }
    }

    fun approveIdea(id: String) {
        viewModelScope.launch {
            handleMutation(ideaRepository.reviewIdea(ReviewIdeaRequest(ideaId = id, approved = true)), "Ideia aprovada.") { loadIdeasForReview() }
        }
    }

    fun rejectIdea(id: String) {
        viewModelScope.launch {
            handleMutation(ideaRepository.reviewIdea(ReviewIdeaRequest(ideaId = id, approved = false, rejectionReason = "Rejeitada pela home.")), "Ideia rejeitada.") { loadIdeasForReview() }
        }
    }

    fun saveProject(title: String, description: String, investment: Double, profit: Double, progress: Int, ownerUid: String) {
        viewModelScope.launch {
            handleMutation(
                projectRepository.saveProject(
                    SaveProjectRequest(
                        title = title.trim(),
                        description = description.trim(),
                        stage = "Execução",
                        status = ProjectStatus.EM_PROGRESSO.name,
                        owner = ownerUid,
                        investment = investment,
                        profit = profit,
                        roi = if (investment > 0) ((profit - investment) / investment) * 100 else 0.0,
                        progress = progress
                    )
                ),
                "Projeto salvo."
            ) { loadProjects() }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private suspend fun <T> handleMutation(result: AppResult<T>, successMessage: String, refresh: suspend () -> Unit) {
        when (result) {
            is AppResult.Success -> {
                _uiState.update { it.copy(message = successMessage) }
                refresh()
            }
            is AppResult.Error -> _uiState.update { it.copy(message = result.message) }
        }
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(
                userRepository = container.userRepository,
                strategyRepository = container.strategyRepository,
                ideaRepository = container.ideaRepository,
                projectRepository = container.projectRepository,
                dashboardRepository = container.dashboardRepository
            ) as T
        }
    }
}
