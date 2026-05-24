@file:OptIn(ExperimentalMaterial3Api::class)

package com.aguiabranca.inovacao.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aguiabranca.inovacao.domain.models.Idea
import com.aguiabranca.inovacao.domain.models.Project
import com.aguiabranca.inovacao.domain.models.Strategy
import com.aguiabranca.inovacao.domain.models.User
import com.aguiabranca.inovacao.domain.models.UserRole
import com.aguiabranca.inovacao.presentation.viewmodel.AppUiState
import com.aguiabranca.inovacao.utils.formatMoney
import com.aguiabranca.inovacao.utils.formatPercent
import com.aguiabranca.inovacao.utils.toRole

@Composable
fun AdminSection(
    users: List<User>,
    onCreateUser: (String, String, String, UserRole) -> Unit,
    onUpdateRole: (String, UserRole) -> Unit,
    onSetActive: (String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.OPERADOR) }

    Section("Gestão de usuários") {
        OutlinedTextField(name, { name = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(email, { email = it }, label = { Text("E-mail") }, modifier = Modifier.fillMaxWidth())
        RoleSelector(selectedRole, onSelect = { selectedRole = it })
        Button(
            onClick = {
                onCreateUser(name, email, "", selectedRole)
                name = ""
                email = ""
            },
            enabled = name.isNotBlank() && email.isNotBlank()
        ) {
            Text("Liberar usuário")
        }
        Divider()
        users.forEach { user ->
            ItemCard {
                Text(user.name.ifBlank { user.email }, style = MaterialTheme.typography.titleMedium)
                Text("${user.email} | ${user.role} | ${if (user.isActive) "Ativo" else "Inativo"}")
                if (user.uid.isNotBlank()) {
                    RoleSelector(user.role.toRole(), onSelect = { onUpdateRole(user.uid, it) })
                    OutlinedButton(onClick = { onSetActive(user.uid, !user.isActive) }) {
                        Text(if (user.isActive) "Desativar" else "Ativar")
                    }
                } else {
                    Text("Pendente de primeiro acesso")
                }
            }
        }
    }
}

@Composable
fun StrategySection(
    strategies: List<Strategy>,
    canManage: Boolean,
    onSave: (String, String, Int) -> Unit,
    onDelete: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("1") }

    Section("Orientações estratégicas") {
        if (canManage) {
            OutlinedTextField(title, { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(description, { description = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                priority,
                { priority = it.filter(Char::isDigit) },
                label = { Text("Prioridade") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    onSave(title, description, priority.toIntOrNull() ?: 1)
                    title = ""
                    description = ""
                    priority = "1"
                },
                enabled = title.isNotBlank() && description.isNotBlank()
            ) {
                Text("Salvar orientação")
            }
        }
        strategies.forEach { strategy ->
            ItemCard {
                Text(strategy.title, style = MaterialTheme.typography.titleMedium)
                Text(strategy.description)
                Text("Prioridade ${strategy.priority}")
                if (canManage) {
                    TextButton(onClick = { onDelete(strategy.id) }) { Text("Excluir") }
                }
            }
        }
    }
}

@Composable
fun IdeaFormSection(onCreateIdea: (String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var impact by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }

    Section("Nova ideia ou problema") {
        OutlinedTextField(title, { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(description, { description = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(impact, { impact = it }, label = { Text("Impacto estimado") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(department, { department = it }, label = { Text("Área") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                onCreateIdea(title, description, impact, department)
                title = ""
                description = ""
                impact = ""
                department = ""
            },
            enabled = title.isNotBlank() && description.isNotBlank()
        ) {
            Text("Registrar ideia")
        }
    }
}

@Composable
fun ReviewIdeasSection(
    ideas: List<Idea>,
    onPrioritize: (String, Int) -> Unit,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    Section("Ideias para avaliação") {
        ideas.forEach { idea ->
            ItemCard {
                Text(idea.title, style = MaterialTheme.typography.titleMedium)
                Text(idea.description)
                Text("${idea.status} | Prioridade ${idea.priority}")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onPrioritize(idea.id, idea.priority + 1) }) { Text("Priorizar") }
                    Button(onClick = { onApprove(idea.id) }) { Text("Aprovar") }
                    OutlinedButton(onClick = { onReject(idea.id) }) { Text("Rejeitar") }
                }
            }
        }
    }
}

@Composable
fun ProjectFormSection(onSaveProject: (String, String, Double, Double, Int) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var investment by remember { mutableStateOf("") }
    var profit by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf("0") }

    Section("Novo projeto/iniciativa") {
        OutlinedTextField(title, { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(description, { description = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(investment, { investment = it }, label = { Text("Investimento") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(profit, { profit = it }, label = { Text("Lucro obtido") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(progress, { progress = it.filter(Char::isDigit) }, label = { Text("Progresso %") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                onSaveProject(title, description, investment.toDoubleOrNull() ?: 0.0, profit.toDoubleOrNull() ?: 0.0, progress.toIntOrNull()?.coerceIn(0, 100) ?: 0)
                title = ""
                description = ""
                investment = ""
                profit = ""
                progress = "0"
            },
            enabled = title.isNotBlank() && description.isNotBlank()
        ) {
            Text("Salvar projeto")
        }
    }
}

@Composable
fun IdeaListSection(title: String, ideas: List<Idea>) {
    Section(title) {
        ideas.forEach { idea ->
            ItemCard {
                Text(idea.title, style = MaterialTheme.typography.titleMedium)
                Text(idea.description)
                Text("${idea.status} | ${idea.department}")
            }
        }
    }
}

@Composable
fun ProjectListSection(title: String, projects: List<Project>) {
    Section(title) {
        projects.forEach { project ->
            ItemCard {
                Text(project.title, style = MaterialTheme.typography.titleMedium)
                Text("${project.stage} | ${project.status} | ${project.progress}%")
                Text("Investimento: R$ ${project.investment.formatMoney()} | ROI: ${project.roi.formatPercent()}")
                Text("Lucro: R$ ${project.profit.formatMoney()}")
            }
        }
    }
}

@Composable
fun DashboardSection(state: AppUiState) {
    Section("Dashboard") {
        val dashboard = state.dashboard
        Text("Projetos: ${dashboard.projectCount}")
        Text("Concluídos: ${dashboard.completedProjects}")
        Text("Investimento total: R$ ${dashboard.totalInvestment.formatMoney()}")
        Text("Lucro obtido: R$ ${dashboard.totalProfit.formatMoney()}")
        Text("ROI médio: ${dashboard.averageRoi.formatPercent()}")
        Text("Redução de custos: R$ ${dashboard.totalCostReduction.formatMoney()}")
        Text("Ganho médio de produtividade: ${dashboard.averageProductivityGain.formatPercent()}")
    }
}
