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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.domain.models.UserRole
import com.aguiabranca.inovacao.presentation.components.ModernCard
import com.aguiabranca.inovacao.presentation.components.Message

@Composable
fun IdeasScreen(appContainer: AppContainer) {
    val viewModel: IdeasViewModel = viewModel(factory = IdeasViewModel.Factory(appContainer))
    val state by viewModel.uiState.collectAsState()
    
    // Assumindo que você tem acesso ao perfil. Para simplificar, vou considerar que o ViewModel 
    // lida com carregar para o perfil atual baseado no token ou passaremos o UserRole.
    // O certo seria ler do SessionManager. Por enquanto vamos usar o authRepo para pegar o User:
    val authViewModel: com.aguiabranca.inovacao.presentation.screens.login.AuthViewModel = viewModel(factory = com.aguiabranca.inovacao.presentation.screens.login.AuthViewModel.Factory(appContainer))
    val authState by authViewModel.uiState.collectAsState()
    val userRole = authState.currentUser?.role ?: UserRole.OPERADOR

    LaunchedEffect(userRole) {
        viewModel.loadIdeas(userRole)
    }

    Scaffold(
        floatingActionButton = {
            if (userRole == UserRole.OPERADOR || userRole == UserRole.GESTOR) {
                FloatingActionButton(onClick = { /* Navegar para CreateIdeaScreen */ }) {
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

                    items(state.ideas) { idea ->
                        ModernCard(onClick = { /* Nav to Detail */ }) {
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
                                Text(
                                    text = idea.status,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
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
}
