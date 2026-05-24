package com.aguiabranca.inovacao.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.aguiabranca.inovacao.presentation.screens.home.HomeScreen
import com.aguiabranca.inovacao.presentation.screens.login.LoginScreen
import com.aguiabranca.inovacao.presentation.viewmodel.AppViewModel

@Composable
fun AppContent(viewModel: AppViewModel) {
    val state by viewModel.uiState.collectAsState()

    if (state.currentUser == null) {
        LoginScreen(
            state = state,
            onLogin = viewModel::login,
            onDismiss = viewModel::dismissMessage
        )
    } else {
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
            onRefresh = { viewModel.loadHomeData() }
        )
    }
}
