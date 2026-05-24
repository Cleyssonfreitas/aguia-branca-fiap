package com.aguiabranca.inovacao.presentation.screens.Detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aguiabranca.inovacao.presentation.components.Message
import com.aguiabranca.inovacao.domain.models.Idea
import com.aguiabranca.inovacao.domain.models.UserRole
import com.aguiabranca.inovacao.domain.models.IdeaStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeaDetailScreen(
    idea: Idea,
    currentUserRole: UserRole,
    onBack: () -> Unit,
    onApproveIdea: (String) -> Unit,
    onRejectIdea: (String, String) -> Unit,
    message: String?,
    onDismissMessage: () -> Unit
) {
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectionReason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Ideia") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Message(message, onDismissMessage)

            // Título e Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = idea.title, style = MaterialTheme.typography.headlineSmall)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statusColor = when (idea.status) {
                            IdeaStatus.APROVADA.name -> Color(0xFF2E7D32)
                            IdeaStatus.REJEITADA.name -> Color(0xFFC62828)
                            IdeaStatus.EM_PROGRESSO.name -> Color(0xFF1565C0)
                            else -> MaterialTheme.colorScheme.primary
                        }
                        SuggestionChip(
                            onClick = {},
                            label = { Text(idea.status) },
                            colors = SuggestionChipDefaults.suggestionChipColors(labelColor = statusColor)
                        )
                        SuggestionChip(
                            onClick = {},
                            label = { Text("Tipo: ${idea.type}") }
                        )
                    }
                }
            }

            // Descrição da Ideia
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Descrição", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Text(text = idea.description, style = MaterialTheme.typography.bodyLarge)
                }
            }

            // Informações Adicionais
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Informações Adicionais", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Text(text = "Área/Departamento: ${idea.department.ifBlank { "Geral" }}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Impacto Estimado: ${idea.estimatedImpact.ifBlank { "Não informado" }}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Visualizações: ${idea.views}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Seção de Rejeição (caso rejeitada)
            if (idea.status == IdeaStatus.REJEITADA.name && !idea.rejectionReason.isNullOrBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "Motivo da Rejeição", style = MaterialTheme.typography.titleMedium, color = Color(0xFFC62828))
                        Text(text = idea.rejectionReason, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFB71C1C))
                    }
                }
            }

            // Ações do Gestor
            if (currentUserRole == UserRole.GESTOR && idea.status == IdeaStatus.SUBMETIDA.name) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Painel de Avaliação do Gestor", style = MaterialTheme.typography.titleMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    onApproveIdea(idea.id)
                                    onBack()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("Aprovar", color = Color.White)
                            }
                            Button(
                                onClick = { showRejectDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                            ) {
                                Text("Rejeitar", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Rejeitar Ideia") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Escreva a justificativa para rejeitar esta ideia:")
                    OutlinedTextField(
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        label = { Text("Motivo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRejectIdea(idea.id, rejectionReason)
                        showRejectDialog = false
                        onBack()
                    },
                    enabled = rejectionReason.isNotBlank()
                ) {
                    Text("Confirmar Rejeição")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
