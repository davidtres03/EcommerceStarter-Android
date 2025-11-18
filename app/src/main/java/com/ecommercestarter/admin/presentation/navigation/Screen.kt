package com.ecommercestarter.admin.presentation.navigation

sealed class Screen(val route: String) {
    object ServerConfig : Screen("server_config")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Orders : Screen("orders")
    object Products : Screen("products")
    object Settings : Screen("settings")
}
