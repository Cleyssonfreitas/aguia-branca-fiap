package com.aguiabranca.inovacao.presentation.screens.projects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.domain.models.Project
import com.aguiabranca.inovacao.domain.models.UserRole
import com.aguiabranca.inovacao.presentation.components.ModernCard
import com.aguiabranca.inovacao.presentation.components.Message

@Composable
fun ProjectsScreen(
    appContainer: AppContainer,
    onNavigateToProjectDetail: (Project) -> Unit
) {
    val viewModel: ProjectsViewModel = viewModel(factory = ProjectsViewModel.Factory(appContainer))
    val state by viewModel.uiState.collectAsState()

    val authViewModel: com.aguiabranca.inovacao.presentation.screens.login.AuthViewModel = viewModel(
        factory = com.aguiabranca.inovacao.presentation.screens.login.AuthViewModel.Factory(appContainer)
    )
    val authState by authViewModel.uiState.collectAsState()
    val userRole = authState.currentUser?.role ?: UserRole.OPERADOR

    var showDialog by remember { mutableStateOf(false) }
    
    // Dialog inputs
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var stage by remember { mutableStateOf("Ideação") }
    var status by remember { mutableStateOf("INICIADO") }
    var investment by remember { mutableStateOf("") }
    var profit by remember { mutableStateOf("") }
    var progress by remember { mutableIntStateOf(0) }
    var selectedIdeas by remember { mutableStateOf<Set<String>>(emptySet()) }

    Scaffold(
        floatingActionButton = {
            if (userRole == UserRole.GESTOR || userRole == UserRole.ADMIN_TI) {
                FloatingActionButton(onClick = { 
                    viewModel.loadApprovedIdeas()
                    showDialog = true 
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Novo Projeto")
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
                            text = "Projetos em Andamento",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (state.projects.isEmpty()) {
                        item {
                            Text(
                                text = "Nenhum projeto cadastrado.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    items(state.projects) { project ->
                        ModernCard(onClick = { onNavigateToProjectDetail(project) }) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = project.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${project.progress}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = project.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Fase: ${project.stage}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "Status: ${project.status}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
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
            title = { Text(text = "Novo Projeto") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título do Projeto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = stage,
                        onValueChange = { stage = it },
                        label = { Text("Etapa (ex: Ideação, Desenvolvimento)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Status (INICIADO, EM_ANDAMENTO, SUSPENSO)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = investment,
                        onValueChange = { investment = it },
                        label = { Text("Investimento Inicial (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = profit,
                        onValueChange = { profit = it },
                        label = { Text("Retorno Esperado (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = if (progress == 0) "" else progress.toString(),
                        onValueChange = { progress = it.toIntOrNull()?.coerceIn(0, 100) ?: 0 },
                        label = { Text("Progresso (0% - 100%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (state.approvedIdeas.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Vincular Ideias Aprovadas:", style = MaterialTheme.typography.titleSmall)
                        state.approvedIdeas.forEach { idea ->
                            val checked = selectedIdeas.contains(idea.id)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedIdeas = if (checked) selectedIdeas - idea.id else selectedIdeas + idea.id
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = {
                                        selectedIdeas = if (it) selectedIdeas + idea.id else selectedIdeas - idea.id
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(idea.title, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createProject(
                            title = title,
                            description = description,
                            stage = stage,
                            status = status,
                            investment = investment.toDoubleOrNull() ?: 0.0,
                            profit = profit.toDoubleOrNull() ?: 0.0,
                            progress = progress,
                            relatedIdeas = selectedIdeas.toList()
                        )
                        showDialog = false
                        title = ""
                        description = ""
                        stage = "Ideação"
                        status = "INICIADO"
                        investment = ""
                        profit = ""
                        progress = 0
                        selectedIdeas = emptySet()
                    },
                    enabled = title.isNotBlank() && description.isNotBlank() && stage.isNotBlank() && status.isNotBlank()
                ) {
                    Text("Salvar")
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
