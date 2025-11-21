package com.ecommercestarter.admin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ecommercestarter.admin.data.preferences.UserPreferences
import com.ecommercestarter.admin.data.repository.BrandingRepository
import com.ecommercestarter.admin.presentation.account.AccountScreen
import com.ecommercestarter.admin.presentation.analytics.AnalyticsScreen
import com.ecommercestarter.admin.presentation.auth.config.ServerConfigScreen
import com.ecommercestarter.admin.presentation.auth.login.LoginScreen
import com.ecommercestarter.admin.presentation.customers.CustomerCreateScreen
import com.ecommercestarter.admin.presentation.customers.CustomerDetailScreen
import com.ecommercestarter.admin.presentation.customers.CustomersScreen
import com.ecommercestarter.admin.presentation.dashboard.DashboardScreen
import com.ecommercestarter.admin.presentation.orders.OrderDetailsScreen
import com.ecommercestarter.admin.presentation.orders.OrdersScreen
import com.ecommercestarter.admin.presentation.products.ProductCreateScreen
import com.ecommercestarter.admin.presentation.products.ProductDetailsScreen
import com.ecommercestarter.admin.presentation.products.ProductEditScreen
import com.ecommercestarter.admin.presentation.products.ProductsScreen
import com.ecommercestarter.admin.presentation.products.variants.VariantListScreen
import com.ecommercestarter.admin.presentation.products.variants.VariantFormScreen
import com.ecommercestarter.admin.presentation.settings.AppSettingsScreen
import com.ecommercestarter.admin.presentation.settings.SettingsScreen
import com.ecommercestarter.admin.presentation.storemanager.*
import com.ecommercestarter.admin.util.BiometricHelper
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun NavGraph(
    navController: NavHostController,
    userPreferences: UserPreferences,
    brandingRepository: BrandingRepository,
    biometricHelper: BiometricHelper,
    activity: FragmentActivity,
    requiresAuth: Boolean = false,
    onAuthComplete: () -> Unit = {}
) {
    val serverUrl by userPreferences.serverUrl.collectAsState(initial = null)
    val authToken by userPreferences.authToken.collectAsState(initial = null)
    val biometricEnabled by userPreferences.biometricEnabled.collectAsState(initial = false)
    
    // Handle background re-authentication requirement
    LaunchedEffect(requiresAuth) {
        android.util.Log.d("NavGraph", "requiresAuth changed to: $requiresAuth")
        android.util.Log.d("NavGraph", "Current route: ${navController.currentDestination?.route}")
        if (requiresAuth && navController.currentDestination?.route != Screen.Login.route) {
            android.util.Log.d("NavGraph", "Navigating to Login for re-authentication")
            // App returned from background - require re-authentication
            navController.navigate(Screen.Login.route) {
                // Don't remove screens from backstack - just show login on top
                launchSingleTop = true
            }
        }
    }
    
    // Determine start destination based on server configuration
    // IMPORTANT: For admin apps, we ALWAYS require authentication
    // We check for token to skip login form, but biometric/password is still required
    val startDestination = when {
        serverUrl.isNullOrEmpty() -> Screen.ServerConfig.route
        else -> Screen.Login.route // Always go to login for security
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
                    onAuthComplete() // Clear requiresAuth flag
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                userPreferences = userPreferences,
                brandingRepository = brandingRepository,
                biometricHelper = biometricHelper,
                activity = activity,
                forceAuth = requiresAuth
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToProducts = {
                    navController.navigate(Screen.Products.route)
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.Orders.route)
                },
                onNavigateToCustomers = {
                    navController.navigate(Screen.Customers.route)
                },
                onNavigateToAnalytics = {
                    navController.navigate(Screen.Analytics.route)
                },
                onNavigateToStoreManager = {
                    navController.navigate(Screen.StoreManager.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                brandingRepository = brandingRepository
            )
        }
        
        composable(Screen.Products.route) {
            ProductsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProductDetails = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                },
                onNavigateToProductCreate = {
                    navController.navigate(Screen.ProductCreate.route)
                },
                onNavigateToCategoryList = {
                    navController.navigate(Screen.CategoryList.route)
                }
            )
        }
        
        composable(Screen.CategoryList.route) {
            com.ecommercestarter.admin.presentation.products.categories.CategoryListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCategoryForm = { categoryId ->
                    navController.navigate(Screen.CategoryForm.createRoute(categoryId))
                }
            )
        }
        
        composable(Screen.CategoryForm.route) { backStackEntry ->
            val categoryIdString = backStackEntry.arguments?.getString("categoryId")
            val categoryId = categoryIdString?.takeIf { it != "null" }?.toIntOrNull()
            
            com.ecommercestarter.admin.presentation.products.categories.CategoryFormScreen(
                categoryId = categoryId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.ProductCreate.route) {
            ProductCreateScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProductCreated = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId)) {
                        popUpTo(Screen.Products.route)
                    }
                }
            )
        }
        
        composable(
            route = Screen.ProductDetails.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductDetailsScreen(
                productId = productId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.ProductEdit.createRoute(id))
                },
                onNavigateToVariants = { id, name ->
                    navController.navigate(Screen.VariantList.createRoute(id, name))
                }
            )
        }
        
        composable(
            route = Screen.ProductEdit.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductEditScreen(
                productId = productId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.VariantList.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType },
                navArgument("productName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            val productName = backStackEntry.arguments?.getString("productName") ?: ""
            VariantListScreen(
                productId = productId,
                productName = productName,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToVariantCreate = { id ->
                    navController.navigate(Screen.VariantCreate.createRoute(id, productName))
                },
                onNavigateToVariantEdit = { id, variantId ->
                    navController.navigate(Screen.VariantEdit.createRoute(id, variantId, productName))
                }
            )
        }
        
        composable(
            route = Screen.VariantCreate.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType },
                navArgument("productName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            val productName = backStackEntry.arguments?.getString("productName") ?: ""
            VariantFormScreen(
                productId = productId,
                variantId = null,
                productName = productName,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.VariantEdit.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType },
                navArgument("variantId") { type = NavType.IntType },
                navArgument("productName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            val variantId = backStackEntry.arguments?.getInt("variantId") ?: 0
            val productName = backStackEntry.arguments?.getString("productName") ?: ""
            VariantFormScreen(
                productId = productId,
                variantId = variantId,
                productName = productName,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Orders.route) {
            OrdersScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToOrderDetails = { orderId ->
                    navController.navigate(Screen.OrderDetails.createRoute(orderId))
                }
            )
        }
        
        composable(
            route = Screen.OrderDetails.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            OrderDetailsScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Customers.route) {
            CustomersScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCustomerCreate = {
                    navController.navigate(Screen.CustomerCreate.route)
                },
                onNavigateToCustomerDetail = { customerId ->
                    navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                }
            )
        }
        
        composable(Screen.CustomerCreate.route) {
            CustomerCreateScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCustomerCreated = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.CustomerDetail.route,
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            CustomerDetailScreen(
                customerId = customerId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToStoreManager = {
                    navController.navigate(Screen.StoreManager.route)
                },
                onNavigateToAccount = {
                    navController.navigate(Screen.Account.route)
                },
                onNavigateToAbout = { /* Handled by dialog in SettingsScreen */ },
                userPreferences = userPreferences,
                brandingRepository = brandingRepository
            )
        }
        
        composable(Screen.StoreManager.route) {
            StoreManagerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAnalytics = {
                    navController.navigate(Screen.Analytics.route)
                },
                onNavigateToServiceDashboard = {
                    navController.navigate(Screen.ServiceDashboard.route)
                },
                onNavigateToUpdateHistory = {
                    navController.navigate(Screen.UpdateHistory.route)
                },
                onNavigateToErrorLog = {
                    navController.navigate(Screen.ErrorLog.route)
                },
                onNavigateToPerformanceMetrics = {
                    navController.navigate(Screen.PerformanceMetrics.route)
                },
                onNavigateToBranding = {
                    navController.navigate(Screen.Branding.route)
                },
                onNavigateToApiConfig = {
                    navController.navigate(Screen.ApiConfig.route)
                },
                onNavigateToSslCertificate = {
                    navController.navigate(Screen.SslCertificate.route)
                },
                onNavigateToSecuritySettings = {
                    navController.navigate(Screen.SecuritySettings.route)
                },
                onNavigateToGoogleAnalytics = {
                    navController.navigate(Screen.GoogleAnalytics.route)
                },
                onNavigateToSecurityAuditLog = {
                    navController.navigate(Screen.SecurityAuditLog.route)
                }
            )
        }
        
        composable(Screen.ServiceDashboard.route) {
            ServiceDashboardScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.UpdateHistory.route) {
            UpdateHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ErrorLog.route) {
            ErrorLogScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.PerformanceMetrics.route) {
            PerformanceMetricsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Branding.route) {
            BrandingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ApiConfig.route) {
            ApiConfigScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.SslCertificate.route) {
            SslCertificateScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.SecuritySettings.route) {
            SecuritySettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.GoogleAnalytics.route) {
            GoogleAnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.SecurityAuditLog.route) {
            SecurityAuditLogScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Account.route) {
            AccountScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                userPreferences = userPreferences
            )
        }
    }
}
