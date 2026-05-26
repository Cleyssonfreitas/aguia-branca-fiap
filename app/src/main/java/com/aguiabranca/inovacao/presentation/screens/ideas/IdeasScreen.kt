package com.aguiabranca.inovacao.presentation.screens.ideas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.domain.models.Idea
import com.aguiabranca.inovacao.domain.models.UserRole
import com.aguiabranca.inovacao.presentation.components.ModernCard
import com.aguiabranca.inovacao.presentation.components.Message

@Composable
fun IdeasScreen(
    appContainer: AppContainer,
    onNavigateToIdeaDetail: (Idea) -> Unit
) {
    val viewModel: IdeasViewModel = viewModel(factory = IdeasViewModel.Factory(appContainer))
    val state by viewModel.uiState.collectAsState()
    
    val authViewModel: com.aguiabranca.inovacao.presentation.screens.login.AuthViewModel = viewModel(
        factory = com.aguiabranca.inovacao.presentation.screens.login.AuthViewModel.Factory(appContainer)
    )
    val authState by authViewModel.uiState.collectAsState()
    val userRole = authState.currentUser?.role ?: UserRole.OPERADOR

    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var impact by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }

    LaunchedEffect(userRole) {
        viewModel.loadIdeas(userRole)
    }

    Scaffold(
        floatingActionButton = {
            if (userRole == UserRole.OPERADOR || userRole == UserRole.GESTOR) {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Ideia")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = if (userRole == UserRole.OPERADOR) "Minhas Ideias" else "Ideias para Revisão",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (state.ideas.isEmpty()) {
                        item {
                            Text(
                                text = "Nenhuma ideia encontrada.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    val sortedIdeas = if (userRole == UserRole.GESTOR) {
                        state.ideas.sortedByDescending { it.aiScore ?: 0 }
                    } else {
                        state.ideas.sortedByDescending { it.createdAt }
                    }

                    items(sortedIdeas) { idea ->
                        ModernCard(onClick = { onNavigateToIdeaDetail(idea) }) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = idea.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = idea.status,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    if (idea.aiScore != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        androidx.compose.material3.Surface(
                                            color = androidx.compose.material3.MaterialTheme.colorScheme.tertiaryContainer,
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "🤖 IA Score: ${idea.aiScore}",
                                                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                                color = androidx.compose.material3.MaterialTheme.colorScheme.onTertiaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = idea.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
            Message(message = state.message, onDismiss = viewModel::dismissMessage)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Nova Ideia / Sugestão") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição do problema/melhoria") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = impact,
                        onValueChange = { impact = it },
                        label = { Text("Impacto estimado (Financeiro/Produtividade)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("Departamento") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createIdea(
                            title = title,
                            description = description,
                            impact = impact,
                            department = department,
                            role = userRole
                        )
                        showDialog = false
                        title = ""
                        description = ""
                        impact = ""
                        department = ""
                    },
                    enabled = title.isNotBlank() && description.isNotBlank() && department.isNotBlank()
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
