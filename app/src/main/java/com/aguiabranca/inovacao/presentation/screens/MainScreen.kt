package com.aguiabranca.inovacao.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aguiabranca.inovacao.domain.models.CurrentUser
import com.aguiabranca.inovacao.domain.models.UserRole

import com.aguiabranca.inovacao.presentation.screens.dashboard.DashboardScreen
import com.aguiabranca.inovacao.presentation.screens.ideas.IdeasScreen
import com.aguiabranca.inovacao.presentation.screens.orientations.OrientationsScreen
import com.aguiabranca.inovacao.presentation.screens.projects.ProjectsScreen
import com.aguiabranca.inovacao.presentation.screens.settings.SettingsScreen
import com.aguiabranca.inovacao.di.AppContainer

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    object Orientations : BottomNavItem("Estratégia", Icons.Default.Map, "orientations")
    object Ideas : BottomNavItem("Ideias", Icons.Default.Lightbulb, "ideas")
    object Projects : BottomNavItem("Projetos", Icons.Default.ListAlt, "projects")
    object Dashboard : BottomNavItem("Dashboard", Icons.Default.Dashboard, "dashboard")
    object Settings : BottomNavItem("Config", Icons.Default.Settings, "settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    currentUser: CurrentUser,
    onNavigateToProfile: () -> Unit,
    navController: NavHostController = rememberNavController(),
    appContainer: AppContainer
) {
    val items = mutableListOf<BottomNavItem>()

    when (currentUser.role) {
        UserRole.OPERADOR -> {
            items.add(BottomNavItem.Ideas)
            items.add(BottomNavItem.Orientations)
        }
        UserRole.GESTOR -> {
            items.add(BottomNavItem.Projects)
            items.add(BottomNavItem.Ideas)
            items.add(BottomNavItem.Orientations)
        }
        UserRole.LIDERANCA -> {
            items.add(BottomNavItem.Dashboard)
            items.add(BottomNavItem.Projects)
            items.add(BottomNavItem.Orientations)
        }
        UserRole.ADMIN_TI -> {
            items.add(BottomNavItem.Dashboard)
            items.add(BottomNavItem.Projects)
            items.add(BottomNavItem.Ideas)
            items.add(BottomNavItem.Orientations)
        }
    }
    items.add(BottomNavItem.Settings)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val currentTitle = items.find { it.route == currentRoute }?.title ?: "Inovação"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentTitle) },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!currentUser.profilePictureUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(currentUser.profilePictureUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val initials = currentUser.name.split(" ")
                                .take(2)
                                .mapNotNull { it.firstOrNull()?.uppercase() }
                                .joinToString("")
                            if (initials.isNotEmpty()) {
                                Text(
                                    text = initials,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Perfil",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = items.first().route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Orientations.route) {
                OrientationsScreen(appContainer)
            }
            composable(BottomNavItem.Ideas.route) {
                IdeasScreen(appContainer)
            }
            composable(BottomNavItem.Projects.route) {
                ProjectsScreen(appContainer)
            }
            composable(BottomNavItem.Dashboard.route) {
                DashboardScreen(appContainer)
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(appContainer)
            }
        }
    }
}
