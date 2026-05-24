package com.aguiabranca.inovacao.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.aguiabranca.inovacao.presentation.screens.home.HomeScreen
import com.aguiabranca.inovacao.presentation.screens.login.LoginScreen
import com.aguiabranca.inovacao.presentation.viewmodel.AppViewModel
import com.aguiabranca.inovacao.domain.models.Idea
import com.aguiabranca.inovacao.domain.models.Project

sealed class Screen {
    object Home : Screen()
    data class IdeaDetail(val idea: Idea) : Screen()
    data class ProjectDetail(val project: Project) : Screen()
}

@Composable
fun AppContent(viewModel: AppViewModel) {
    val state by viewModel.uiState.collectAsState()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    if (state.currentUser == null) {
        currentScreen = Screen.Home
        LoginScreen(
            state = state,
            onLogin = viewModel::login,
            onDismiss = viewModel::dismissMessage
        )
    } else {
        val userRole = state.currentUser?.role ?: com.aguiabranca.inovacao.domain.models.UserRole.OPERADOR
        when (val screen = currentScreen) {
            is Screen.Home -> {
                HomeScreen(
                    state = state,
                    onLogout = viewModel::logout,
                    onDismiss = viewModel::dismissMessage,
                    onCreateUser = viewModel::createUser,
                    onUpdateRole = viewModel::updateUserRole,
                    onSetActive = viewModel::setUserActive,
                    onSaveStrategy = viewModel::saveStrategy,
                    onDeleteStrategy = viewModel::deleteStrategy,
                    onCreateIdea = viewModel::createIdea,
                    onPrioritizeIdea = viewModel::prioritizeIdea,
                    onApproveIdea = viewModel::approveIdea,
                    onRejectIdea = viewModel::rejectIdea,
                    onSaveProject = viewModel::saveProject,
                    onRefresh = { viewModel.loadHomeData() },
                    onSelectIdea = { currentScreen = Screen.IdeaDetail(it) },
                    onSelectProject = { currentScreen = Screen.ProjectDetail(it) }
                )
            }
            is Screen.IdeaDetail -> {
                val latestIdea = state.myIdeas.firstOrNull { it.id == screen.idea.id }
                    ?: state.reviewIdeas.firstOrNull { it.id == screen.idea.id }
                    ?: screen.idea
                IdeaDetailScreen(
                    idea = latestIdea,
                    currentUserRole = userRole,
                    onBack = { currentScreen = Screen.Home },
                    onApproveIdea = viewModel::approveIdea,
                    onRejectIdea = { id, reason -> viewModel.rejectIdea(id, reason) },
                    message = state.message,
                    onDismissMessage = viewModel::dismissMessage
                )
            }
            is Screen.ProjectDetail -> {
                val latestProject = state.projects.firstOrNull { it.id == screen.project.id }
                    ?: screen.project
                ProjectDetailScreen(
                    project = latestProject,
                    currentUserRole = userRole,
                    onBack = { currentScreen = Screen.Home },
                    onUpdateProject = { id, title, desc, stage, status, investment, profit, progress ->
                        viewModel.updateProject(id, title, desc, stage, status, investment, profit, progress)
                    },
                    message = state.message,
                    onDismissMessage = viewModel::dismissMessage
                )
            }
        }
    }
}
