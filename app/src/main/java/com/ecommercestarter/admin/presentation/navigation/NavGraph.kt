package com.ecommercestarter.admin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ecommercestarter.admin.data.preferences.UserPreferences
import com.ecommercestarter.admin.presentation.auth.config.ServerConfigScreen
import com.ecommercestarter.admin.presentation.auth.login.LoginScreen
import com.ecommercestarter.admin.presentation.dashboard.DashboardScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    userPreferences: UserPreferences
) {
    val serverUrl by userPreferences.serverUrl.collectAsState(initial = null)
    
    // Determine start destination based on server configuration
    val startDestination = if (serverUrl.isNullOrEmpty()) {
        Screen.ServerConfig.route
    } else {
        Screen.Login.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.ServerConfig.route) {
            ServerConfigScreen(
                onConfigured = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ServerConfig.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
    }
}
