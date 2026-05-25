package com.aguiabranca.inovacao.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.presentation.screens.home.HomeScreen
import com.aguiabranca.inovacao.presentation.screens.login.LoginScreen
import com.aguiabranca.inovacao.presentation.screens.detail.IdeaDetailScreen
import com.aguiabranca.inovacao.presentation.screens.detail.ProjectDetailScreen
import com.aguiabranca.inovacao.presentation.screens.login.AuthViewModel
import com.aguiabranca.inovacao.presentation.screens.home.HomeViewModel
import com.aguiabranca.inovacao.presentation.screens.detail.IdeaDetailViewModel
import com.aguiabranca.inovacao.presentation.screens.detail.ProjectDetailViewModel
import com.aguiabranca.inovacao.presentation.screens.profile.ProfileScreen
import com.aguiabranca.inovacao.presentation.screens.profile.ProfileViewModel
import com.aguiabranca.inovacao.domain.models.Idea
import com.aguiabranca.inovacao.domain.models.Project
import com.google.gson.Gson

@Composable
fun AppContent(appContainer: AppContainer) {
    val navController = rememberNavController()

    // Root ViewModel for Authentication
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory(appContainer))
    val authState by authViewModel.uiState.collectAsState()

    // Determine start destination
    val startDestination = if (authState.currentUser != null) "home" else "login"

    // Only render NavHost when auth check is not loading
    if (authState.isLoading) {
        // Podia ser uma splash screen
        return
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                state = authState,
                onLogin = authViewModel::login,
                onDismiss = authViewModel::dismissMessage
            )
        }

        composable("home") {
            if (authState.currentUser != null) {
                com.aguiabranca.inovacao.presentation.screens.MainScreen(
                    currentUser = authState.currentUser!!,
                    onNavigateToProfile = {
                        navController.navigate("profile")
                    },
                    appContainer = appContainer
                    // In the future we will pass navigateToIdeaDetail and navigateToProjectDetail here
                    // so that the bottom navigation screens can navigate to details in the parent NavHost
                )
            }
        }

        composable(
            route = "ideaDetail/{ideaJson}",
            arguments = listOf(navArgument("ideaJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val ideaJson = backStackEntry.arguments?.getString("ideaJson")
            val idea = Gson().fromJson(ideaJson, Idea::class.java)
            
            val ideaDetailViewModel: IdeaDetailViewModel = viewModel(factory = IdeaDetailViewModel.Factory(appContainer))
            val ideaState by ideaDetailViewModel.uiState.collectAsState()
            val userRole = authState.currentUser?.role ?: com.aguiabranca.inovacao.domain.models.UserRole.OPERADOR

            IdeaDetailScreen(
                idea = idea,
                currentUserRole = userRole,
                onBack = { navController.popBackStack() },
                onApproveIdea = { id -> 
                    ideaDetailViewModel.approveIdea(id) 
                },
                onRejectIdea = { id, reason -> 
                    ideaDetailViewModel.rejectIdea(id, reason) 
                },
                message = ideaState.message,
                onDismissMessage = ideaDetailViewModel::dismissMessage
            )
        }

        composable(
            route = "projectDetail/{projectJson}",
            arguments = listOf(navArgument("projectJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectJson = backStackEntry.arguments?.getString("projectJson")
            val project = Gson().fromJson(projectJson, Project::class.java)

            val projectDetailViewModel: ProjectDetailViewModel = viewModel(factory = ProjectDetailViewModel.Factory(appContainer))
            val projectState by projectDetailViewModel.uiState.collectAsState()
            val userRole = authState.currentUser?.role ?: com.aguiabranca.inovacao.domain.models.UserRole.OPERADOR

            ProjectDetailScreen(
                project = project,
                currentUserRole = userRole,
                onBack = { navController.popBackStack() },
                onUpdateProject = { id, title, desc, stage, status, investment, profit, progress ->
                    projectDetailViewModel.updateProject(id, title, desc, stage, status, investment, profit, progress)
                },
                message = projectState.message,
                onDismissMessage = projectDetailViewModel::dismissMessage
            )
        }

        composable("profile") {
            if (authState.currentUser != null) {
                val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(appContainer))
                val profileState by profileViewModel.uiState.collectAsState()
                
                ProfileScreen(
                    currentUser = authState.currentUser!!,
                    uiState = profileState,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    },
                    onUpdatePicture = { uri ->
                        profileViewModel.updateProfilePicture(uri) {
                            authViewModel.refreshUser()
                        }
                    },
                    onDeletePicture = {
                        profileViewModel.deleteProfilePicture {
                            authViewModel.refreshUser()
                        }
                    },
                    onDismissMessage = profileViewModel::dismissMessage
                )
            }
        }
    }
}
