@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.aguiabranca.inovacao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.models.Idea
import com.aguiabranca.inovacao.models.Project
import com.aguiabranca.inovacao.models.Strategy
import com.aguiabranca.inovacao.models.User
import com.aguiabranca.inovacao.models.UserRole
import com.aguiabranca.inovacao.ui.AppUiState
import com.aguiabranca.inovacao.ui.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = AppContainer(applicationContext)
        val viewModel = ViewModelProvider(this, AppViewModel.Factory(container))[AppViewModel::class.java]
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AguiaBrancaApp(viewModel)
                }
            }
        }
    }
}

@Composable
private fun AguiaBrancaApp(viewModel: AppViewModel) {
    val state by viewModel.uiState.collectAsState()

    if (state.currentUser == null) {
        LoginScreen(state = state, onLogin = viewModel::login, onDismiss = viewModel::dismissMessage)
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

@Composable
private fun LoginScreen(
    state: AppUiState,
    onLogin: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Águia Branca Inovação", style = MaterialTheme.typography.headlineSmall)
        Text("Entrar ou completar primeiro acesso", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { onLogin(email, password) },
            enabled = !state.isLoading && email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isLoading) "Entrando..." else "Entrar")
        }
        Message(state.message, onDismiss)
    }
}

@Composable
private fun HomeScreen(
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
    onRefresh: () -> Unit
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
                ProjectListSection("Projetos para suporte", state.projects)
                DashboardSection(state)
            }
            UserRole.LIDERANCA -> {
                ProjectListSection("Andamento dos projetos", state.projects)
                DashboardSection(state)
            }
            UserRole.GESTOR -> {
                ReviewIdeasSection(state.reviewIdeas, onPrioritizeIdea, onApproveIdea, onRejectIdea)
                ProjectFormSection(onSaveProject)
                ProjectListSection("Projetos", state.projects)
            }
            UserRole.OPERADOR -> {
                IdeaFormSection(onCreateIdea)
                IdeaListSection("Minhas ideias", state.myIdeas)
            }
        }
    }
}

@Composable
private fun AdminSection(
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
private fun StrategySection(
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
private fun IdeaFormSection(onCreateIdea: (String, String, String, String) -> Unit) {
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
private fun ReviewIdeasSection(
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
private fun ProjectFormSection(onSaveProject: (String, String, Double, Double, Int) -> Unit) {
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
private fun IdeaListSection(title: String, ideas: List<Idea>) {
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
private fun ProjectListSection(title: String, projects: List<Project>) {
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
private fun DashboardSection(state: AppUiState) {
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

@Composable
private fun RoleSelector(selected: UserRole, onSelect: (UserRole) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        UserRole.entries.forEach { role ->
            if (role == selected) {
                Button(onClick = { onSelect(role) }, modifier = Modifier.weight(1f)) { Text(role.shortName()) }
            } else {
                OutlinedButton(onClick = { onSelect(role) }, modifier = Modifier.weight(1f)) { Text(role.shortName()) }
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        content()
    }
}

@Composable
private fun ItemCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp), content = content)
    }
}

@Composable
private fun Message(message: String?, onDismiss: () -> Unit) {
    if (message != null) {
        Spacer(Modifier.height(8.dp))
        ItemCard {
            Text(message)
            TextButton(onClick = onDismiss) { Text("Ok") }
        }
    }
}

private fun String.toRole(): UserRole = UserRole.entries.firstOrNull { it.name == this } ?: UserRole.OPERADOR
private fun UserRole.shortName(): String = when (this) {
    UserRole.ADMIN_TI -> "TI"
    UserRole.LIDERANCA -> "Líder"
    UserRole.GESTOR -> "Gestor"
    UserRole.OPERADOR -> "Operador"
}

private fun Double.formatMoney(): String = String.format("%.2f", this)
private fun Double.formatPercent(): String = String.format("%.1f%%", this)
