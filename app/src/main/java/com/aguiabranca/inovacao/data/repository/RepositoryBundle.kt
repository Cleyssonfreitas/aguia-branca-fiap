package com.aguiabranca.inovacao.data.repository

import android.content.Context
import com.aguiabranca.inovacao.data.local.AppDatabase
import com.aguiabranca.inovacao.domain.repository.AuthRepository
import com.aguiabranca.inovacao.domain.repository.DashboardRepository
import com.aguiabranca.inovacao.domain.repository.IdeaRepository
import com.aguiabranca.inovacao.domain.repository.ProjectRepository
import com.aguiabranca.inovacao.domain.repository.StrategyRepository
import com.aguiabranca.inovacao.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson

data class RepositoryBundle(
    val authRepository: AuthRepository,
    val userRepository: UserRepository,
    val strategyRepository: StrategyRepository,
    val ideaRepository: IdeaRepository,
    val projectRepository: ProjectRepository,
    val dashboardRepository: DashboardRepository
)

fun createFirebaseRepositoryBundle(context: Context): RepositoryBundle {
    val database = AppDatabase.getInstance(context)
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseDb = FirebaseDatabase.getInstance()
    val gson = Gson()

    return RepositoryBundle(
        authRepository = FirebaseAuthRepository(
            firebaseAuth = firebaseAuth,
            firebaseDb = firebaseDb,
            userDao = database.userDao()
        ),
        userRepository = FirebaseUserRepository(
            firebaseAuth = firebaseAuth,
            firebaseDb = firebaseDb,
            userDao = database.userDao()
        ),
        strategyRepository = FirebaseStrategyRepository(
            firebaseAuth = firebaseAuth,
            firebaseDb = firebaseDb,
            strategyDao = database.strategyDao()
        ),
        ideaRepository = FirebaseIdeaRepository(
            firebaseAuth = firebaseAuth,
            firebaseDb = firebaseDb,
            ideaDao = database.ideaDao(),
            pendingActionDao = database.pendingActionDao(),
            gson = gson
        ),
        projectRepository = FirebaseProjectRepository(
            firebaseAuth = firebaseAuth,
            firebaseDb = firebaseDb,
            projectDao = database.projectDao()
        ),
        dashboardRepository = FirebaseDashboardRepository(
            firebaseAuth = firebaseAuth,
            firebaseDb = firebaseDb,
            dashboardDao = database.dashboardDao()
        )
    )
}
