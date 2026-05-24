package com.aguiabranca.inovacao.domain

data class DashboardSummary(
    val projectCount: Int = 0,
    val completedProjects: Int = 0,
    val totalInvestment: Double = 0.0,
    val totalProfit: Double = 0.0,
    val averageRoi: Double = 0.0,
    val totalCostReduction: Double = 0.0,
    val averageProductivityGain: Double = 0.0
)

