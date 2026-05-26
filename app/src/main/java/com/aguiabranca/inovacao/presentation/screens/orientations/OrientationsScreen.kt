package com.aguiabranca.inovacao.presentation.screens.orientations

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.aguiabranca.inovacao.domain.models.Strategy
import com.aguiabranca.inovacao.domain.models.UserRole
import com.aguiabranca.inovacao.presentation.components.ModernCard
import com.aguiabranca.inovacao.presentation.components.Message

@Composable
fun OrientationsScreen(
    appContainer: AppContainer
) {
    val viewModel: OrientationsViewModel = viewModel(factory = OrientationsViewModel.Factory(appContainer))
    val state by viewModel.uiState.collectAsState()

    val authViewModel: com.aguiabranca.inovacao.presentation.screens.login.AuthViewModel = viewModel(
        factory = com.aguiabranca.inovacao.presentation.screens.login.AuthViewModel.Factory(appContainer)
    )
    val authState by authViewModel.uiState.collectAsState()
    val userRole = authState.currentUser?.role ?: com.aguiabranca.inovacao.domain.models.UserRole.OPERADOR

    LaunchedEffect(Unit) {
        viewModel.loadStrategies()
    }

    var showDialog by remember { mutableStateOf(false) }
    var editingStrategy by remember { mutableStateOf<Strategy?>(null) }
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableIntStateOf(1) }

    // Open dialog for editing
    val openEditDialog = { strategy: Strategy ->
        editingStrategy = strategy
        title = strategy.title
        description = strategy.description
        priority = strategy.priority
        showDialog = true
    }

    // Open dialog for creating
    val openCreateDialog = {
        editingStrategy = null
        title = ""
        description = ""
        priority = 1
        showDialog = true
    }

    Scaffold(
        floatingActionButton = {
            if (userRole == UserRole.LIDERANCA || userRole == UserRole.ADMIN_TI) {
                FloatingActionButton(onClick = openCreateDialog) {
                    Icon(Icons.Default.Add, contentDescription = "Nova Orientação")
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
                            text = "Orientações Estratégicas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (state.strategies.isEmpty()) {
                        item {
                            Text(
                                text = "Nenhuma orientação cadastrada.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    items(state.strategies) { strategy ->
                        ModernCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = strategy.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                if (userRole == UserRole.LIDERANCA || userRole == UserRole.ADMIN_TI) {
                                    Row {
                                        IconButton(onClick = { openEditDialog(strategy) }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                                        }
                                        IconButton(onClick = { viewModel.deleteStrategy(strategy.id) }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Excluir",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = strategy.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Prioridade: ${strategy.priority}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
            title = { Text(text = if (editingStrategy == null) "Nova Orientação" else "Editar Orientação") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = if (priority == 0) "" else priority.toString(),
                        onValueChange = { priority = it.toIntOrNull() ?: 0 },
                        label = { Text("Prioridade (1 - Alta, 5 - Baixa)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveStrategy(
                            title = title,
                            description = description,
                            priority = priority,
                            id = editingStrategy?.id ?: ""
                        )
                        showDialog = false
                    },
                    enabled = title.isNotBlank() && description.isNotBlank() && priority > 0
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
