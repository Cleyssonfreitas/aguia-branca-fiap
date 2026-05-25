package com.aguiabranca.inovacao.presentation.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aguiabranca.inovacao.presentation.components.Message
import com.aguiabranca.inovacao.domain.usecase.ProjectCalculationsUseCase
import com.aguiabranca.inovacao.domain.models.Project
import com.aguiabranca.inovacao.domain.models.UserRole
import com.aguiabranca.inovacao.domain.models.ProjectStatus
import com.aguiabranca.inovacao.utils.formatMoney
import com.aguiabranca.inovacao.utils.formatPercent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    project: Project,
    currentUserRole: UserRole,
    onBack: () -> Unit,
    onUpdateProject: (String, String, String, String, String, Double, Double, Int) -> Unit,
    message: String?,
    onDismissMessage: () -> Unit
) {
    val canEdit = currentUserRole == UserRole.GESTOR || currentUserRole == UserRole.LIDERANCA

    var title by remember { mutableStateOf(project.title) }
    var description by remember { mutableStateOf(project.description) }
    var stage by remember { mutableStateOf(project.stage) }
    var status by remember { mutableStateOf(project.status) }
    var investmentStr by remember { mutableStateOf(project.investment.toString()) }
    var profitStr by remember { mutableStateOf(project.profit.toString()) }
    var progressVal by remember { mutableStateOf(project.progress.toFloat()) }

    val investment = investmentStr.toDoubleOrNull() ?: 0.0
    val profit = profitStr.toDoubleOrNull() ?: 0.0
    val calculatedRoi = ProjectCalculationsUseCase.calculateROI(investment, profit)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Projeto") },
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

            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (canEdit) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título do Projeto") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(text = title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Text("Etapa: $stage | Status: $status", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Descrição
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Descrição", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    if (canEdit) {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Editar Descrição") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4
                        )
                    } else {
                        Text(text = description, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            // Progresso Visual
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Progresso do Projeto", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text(text = "${progressVal.toInt()}%", style = MaterialTheme.typography.titleLarge)
                    }
                    LinearProgressIndicator(
                        progress = progressVal / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                    if (canEdit) {
                        Slider(
                            value = progressVal,
                            onValueChange = { progressVal = it },
                            valueRange = 0f..100f,
                            steps = 100
                        )
                    }
                }
            }

            // Métricas Financeiras
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Indicadores Financeiros", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                    if (canEdit) {
                        OutlinedTextField(
                            value = investmentStr,
                            onValueChange = { investmentStr = it },
                            label = { Text("Investimento (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = profitStr,
                            onValueChange = { profitStr = it },
                            label = { Text("Retorno Real / Lucro (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Investimento:")
                            Text("R$ ${project.investment.formatMoney()}")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Retorno / Lucro:")
                            Text("R$ ${project.profit.formatMoney()}")
                        }
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ROI Estimado:", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = calculatedRoi.formatPercent(),
                            style = MaterialTheme.typography.titleLarge,
                            color = if (calculatedRoi >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }

            // Status e Etapa Editores (Gestores)
            if (canEdit) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "Configuração do Projeto", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                        OutlinedTextField(
                            value = stage,
                            onValueChange = { stage = it },
                            label = { Text("Etapa (Ex: Planejamento, Execução, Homologação)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Status do Projeto:")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val statuses = listOf(ProjectStatus.EM_PROGRESSO.name, ProjectStatus.PAUSADO.name, ProjectStatus.CONCLUIDO.name)
                            statuses.forEach { s ->
                                FilterChip(
                                    selected = status == s,
                                    onClick = { status = s },
                                    label = { Text(s.replace("_", " ")) }
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        onUpdateProject(
                            project.id,
                            title,
                            description,
                            stage,
                            status,
                            investment,
                            profit,
                            progressVal.toInt()
                        )
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank() && description.isNotBlank()
                ) {
                    Text("Salvar Alterações")
                }
            }
        }
    }
}
