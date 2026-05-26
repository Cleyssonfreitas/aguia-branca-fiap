package com.aguiabranca.inovacao.di

import android.content.Context
import com.aguiabranca.inovacao.data.repository.createFirebaseRepositoryBundle
import com.aguiabranca.inovacao.domain.repository.AuthRepository
import com.aguiabranca.inovacao.domain.repository.DashboardRepository
import com.aguiabranca.inovacao.domain.repository.IdeaRepository
import com.aguiabranca.inovacao.domain.repository.ProjectRepository
import com.aguiabranca.inovacao.domain.repository.StrategyRepository
import com.aguiabranca.inovacao.domain.repository.UserRepository

import com.aguiabranca.inovacao.data.local.datastore.SessionManager

class AppContainer(val context: Context) {
    val sessionManager = SessionManager(context)
    private val repositories = createFirebaseRepositoryBundle(context)

    val authRepository: AuthRepository = repositories.authRepository

    val userRepository: UserRepository = repositories.userRepository

    val strategyRepository: StrategyRepository = repositories.strategyRepository

    val ideaRepository: IdeaRepository = repositories.ideaRepository

    val projectRepository: ProjectRepository = repositories.projectRepository

    val dashboardRepository: DashboardRepository = repositories.dashboardRepository

    val evaluateIdeaUseCase = com.aguiabranca.inovacao.domain.usecase.EvaluateIdeaUseCase(
        com.aguiabranca.inovacao.network.RetrofitClient.getApiService()
    )
}
