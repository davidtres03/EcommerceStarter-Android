package com.ecommercestarter.admin.presentation.storemanager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreManagerScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToServiceDashboard: () -> Unit = {},
    onNavigateToUpdateHistory: () -> Unit = {},
    onNavigateToErrorLog: () -> Unit = {},
    onNavigateToPerformanceMetrics: () -> Unit = {},
    onNavigateToBranding: () -> Unit = {},
    onNavigateToApiConfig: () -> Unit = {},
    onNavigateToSslCertificate: () -> Unit = {},
    onNavigateToSecuritySettings: () -> Unit = {},
    onNavigateToGoogleAnalytics: () -> Unit = {},
    onNavigateToSecurityAuditLog: () -> Unit = {}
) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Store Manager", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // System Monitoring Section
            item {
                Text(
                    "System Monitoring",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            item {
                SystemCard(
                    title = "Analytics",
                    description = "Visitor tracking & site analytics",
                    icon = Icons.Default.Info,
                    onClick = onNavigateToAnalytics
                )
            }
            
            item {
                SystemCard(
                    title = "Service Dashboard",
                    description = "Real-time system health & status",
                    icon = Icons.Default.Refresh,
                    onClick = onNavigateToServiceDashboard
                )
            }
            
            item {
                SystemCard(
                    title = "Update History",
                    description = "View application updates & rollbacks",
                    icon = Icons.Default.KeyboardArrowUp,
                    onClick = onNavigateToUpdateHistory
                )
            }
            
            item {
                SystemCard(
                    title = "Error Log",
                    description = "Monitor and review system errors",
                    icon = Icons.Default.Warning,
                    onClick = onNavigateToErrorLog
                )
            }
            
            item {
                SystemCard(
                    title = "Performance Metrics",
                    description = "24h performance analytics",
                    icon = Icons.Default.Info,
                    onClick = onNavigateToPerformanceMetrics
                )
            }
            
            // Settings & Configuration Section
            item {
                Text(
                    "Settings & Configuration",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            item {
                SystemCard(
                    title = "Branding & Theme",
                    description = "Customize site appearance & colors",
                    icon = Icons.Default.Edit,
                    onClick = onNavigateToBranding
                )
            }
            
            item {
                SystemCard(
                    title = "API Configurations",
                    description = "Stripe, Cloudinary, USPS, UPS, FedEx, AI Services",
                    icon = Icons.Default.Settings,
                    onClick = onNavigateToApiConfig
                )
            }
            
            item {
                SystemCard(
                    title = "SSL Certificate",
                    description = "Manage SSL/TLS certificates",
                    icon = Icons.Default.Lock,
                    onClick = onNavigateToSslCertificate
                )
            }
            
            item {
                SystemCard(
                    title = "Security Settings",
                    description = "Configure security & intrusion detection",
                    icon = Icons.Default.Lock,
                    onClick = onNavigateToSecuritySettings
                )
            }
            
            item {
                SystemCard(
                    title = "Google Analytics",
                    description = "Configure Google Analytics tracking",
                    icon = Icons.Default.Info,
                    onClick = onNavigateToGoogleAnalytics
                )
            }
            
            item {
                SystemCard(
                    title = "Security Audit Log",
                    description = "View security events & blocked IPs",
                    icon = Icons.Default.DateRange,
                    onClick = onNavigateToSecurityAuditLog
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun SystemCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
