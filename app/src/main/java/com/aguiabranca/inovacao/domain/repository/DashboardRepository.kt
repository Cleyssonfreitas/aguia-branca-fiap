package com.aguiabranca.inovacao.domain.repository

import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.DashboardSummary

interface DashboardRepository {
    suspend fun getLeadershipDashboard(): AppResult<DashboardSummary>
}

