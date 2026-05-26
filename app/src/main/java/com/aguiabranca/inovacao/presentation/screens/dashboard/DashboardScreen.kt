package com.aguiabranca.inovacao.presentation.screens.dashboard

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.presentation.components.CustomBarChart
import com.aguiabranca.inovacao.presentation.components.ModernCard
import com.aguiabranca.inovacao.presentation.components.Message

@Composable
fun DashboardScreen(appContainer: AppContainer) {
    val viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory(appContainer))
    val state by viewModel.uiState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    Scaffold { paddingValues ->
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
                            text = "Dashboard de Resultados",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    state.summary?.let { summary ->
                        item {
                            ModernCard {
                                Text(
                                    text = "Resumo Geral",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Projetos Ativos", style = MaterialTheme.typography.labelSmall)
                                        Text(summary.projectCount.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text("ROI Médio", style = MaterialTheme.typography.labelSmall)
                                        Text("${String.format("%.2f", summary.averageRoi)}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Investimento", style = MaterialTheme.typography.labelSmall)
                                        Text("R$ ${String.format("%.2f", summary.totalInvestment)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text("Lucro Total", style = MaterialTheme.typography.labelSmall)
                                        Text("R$ ${String.format("%.2f", summary.totalProfit)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        ModernCard {
                            Text(
                                text = "Volume de Projetos por Mês",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            val chartData = listOf(
                                "Jan" to 2f,
                                "Fev" to 4f,
                                "Mar" to 3f,
                                "Abr" to 6f,
                                "Mai" to (state.summary?.projectCount?.toFloat() ?: 0f)
                            )
                            CustomBarChart(
                                data = chartData,
                                modifier = Modifier.fillMaxWidth().height(200.dp)
                            )
                        }
                    }
                }
            }
            Message(message = state.message, onDismiss = viewModel::dismissMessage)
        }
    }
}
