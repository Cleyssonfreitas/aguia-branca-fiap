package com.aguiabranca.inovacao.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aguiabranca.inovacao.presentation.components.Message
import com.aguiabranca.inovacao.domain.models.Idea
import com.aguiabranca.inovacao.domain.models.Project
import com.aguiabranca.inovacao.domain.models.UserRole
import com.aguiabranca.inovacao.presentation.components.AdminSection
import com.aguiabranca.inovacao.presentation.components.DashboardSection
import com.aguiabranca.inovacao.presentation.components.IdeaFormSection
import com.aguiabranca.inovacao.presentation.components.IdeaListSection
import com.aguiabranca.inovacao.presentation.components.ProjectFormSection
import com.aguiabranca.inovacao.presentation.components.ProjectListSection
import com.aguiabranca.inovacao.presentation.components.ReviewIdeasSection
import com.aguiabranca.inovacao.presentation.components.StrategySection
import com.aguiabranca.inovacao.presentation.viewmodel.AppUiState

@Composable
fun HomeScreen(
    state: AppUiState,
    onLogout: () -> Unit,
    onDismiss: () -> Unit,
    onCreateUser: (String, String, String, UserRole) -> Unit,
    onUpdateRole: (String, UserRole) -> Unit,
    onSetActive: (String, Boolean) -> Unit,
    onSaveStrategy: (String, String, Int) -> Unit,
    onDeleteStrategy: (String) -> Unit,
    onCreateIdea: (String, String, String, String) -> Unit,
    onPrioritizeIdea: (String, Int) -> Unit,
    onApproveIdea: (String) -> Unit,
    onRejectIdea: (String) -> Unit,
    onSaveProject: (String, String, Double, Double, Int) -> Unit,
    onRefresh: () -> Unit,
    onSelectIdea: (Idea) -> Unit,
    onSelectProject: (Project) -> Unit
) {
    val user = state.currentUser ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(user.name.ifBlank { user.email }, style = MaterialTheme.typography.titleLarge)
                Text(user.role.name, style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                OutlinedButton(onClick = onRefresh) { Text("Atualizar") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onLogout) { Text("Sair") }
            }
        }

        Message(state.message, onDismiss)
        StrategySection(
            strategies = state.strategies,
            canManage = user.role == UserRole.LIDERANCA,
            onSave = onSaveStrategy,
            onDelete = onDeleteStrategy
        )

        when (user.role) {
            UserRole.ADMIN_TI -> {
                AdminSection(state.users, onCreateUser, onUpdateRole, onSetActive)
                ProjectListSection("Projetos para suporte", state.projects, onSelectProject)
                DashboardSection(state)
            }
            UserRole.LIDERANCA -> {
                ProjectListSection("Andamento dos projetos", state.projects, onSelectProject)
                DashboardSection(state)
            }
            UserRole.GESTOR -> {
                ReviewIdeasSection(state.reviewIdeas, onPrioritizeIdea, onApproveIdea, onRejectIdea, onSelectIdea)
                ProjectFormSection(onSaveProject)
                ProjectListSection("Projetos", state.projects, onSelectProject)
            }
            UserRole.OPERADOR -> {
                IdeaFormSection(onCreateIdea)
                IdeaListSection("Minhas ideias", state.myIdeas, onSelectIdea)
            }
        }
    }
}

