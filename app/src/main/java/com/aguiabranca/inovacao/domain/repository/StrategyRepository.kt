package com.aguiabranca.inovacao.domain.repository

import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.SaveStrategyRequest
import com.aguiabranca.inovacao.models.Strategy

interface StrategyRepository {
    suspend fun listStrategies(): AppResult<List<Strategy>>
    suspend fun saveStrategy(request: SaveStrategyRequest): AppResult<Strategy>
    suspend fun deleteStrategy(strategyId: String): AppResult<Unit>
}

