package com.ecommercestarter.admin.presentation.navigation

sealed class Screen(val route: String) {
    object ServerConfig : Screen("server_config")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Products : Screen("products")
    object ProductCreate : Screen("product_create")
    object CategoryList : Screen("category_list")
    object CategoryForm : Screen("category_form/{categoryId}") {
        fun createRoute(categoryId: Int?) = if (categoryId != null) "category_form/$categoryId" else "category_form/null"
    }
    object ProductEdit : Screen("product_edit/{productId}") {
        fun createRoute(productId: Int) = "product_edit/$productId"
    }
    object ProductDetails : Screen("product_details/{productId}") {
        fun createRoute(productId: Int) = "product_details/$productId"
    }
    object Orders : Screen("orders")
    object OrderDetails : Screen("order_details/{orderId}") {
        fun createRoute(orderId: Int) = "order_details/$orderId"
    }
    object Customers : Screen("customers")
    object CustomerCreate : Screen("customer_create")
    object CustomerDetail : Screen("customer_detail/{customerId}") {
        fun createRoute(customerId: String) = "customer_detail/$customerId"
    }
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")
    object StoreManager : Screen("store_manager")
    object Account : Screen("account")
    
    // Store Manager Sub-Screens
    object StoreAnalytics : Screen("store_analytics")
    object ServiceDashboard : Screen("service_dashboard")
    object UpdateHistory : Screen("update_history")
    object ErrorLog : Screen("error_log")
    object PerformanceMetrics : Screen("performance_metrics")
    object Branding : Screen("branding")
    object ApiConfig : Screen("api_config")
    object SslCertificate : Screen("ssl_certificate")
    object SecuritySettings : Screen("security_settings")
    object GoogleAnalytics : Screen("google_analytics")
    object SecurityAuditLog : Screen("security_audit_log")
    
    // Variant Screens
    object VariantList : Screen("variant_list/{productId}/{productName}") {
        fun createRoute(productId: Int, productName: String) = "variant_list/$productId/$productName"
    }
    object VariantCreate : Screen("variant_create/{productId}/{productName}") {
        fun createRoute(productId: Int, productName: String) = "variant_create/$productId/$productName"
    }
    object VariantEdit : Screen("variant_edit/{productId}/{variantId}/{productName}") {
        fun createRoute(productId: Int, variantId: Int, productName: String) = "variant_edit/$productId/$variantId/$productName"
    }
}
