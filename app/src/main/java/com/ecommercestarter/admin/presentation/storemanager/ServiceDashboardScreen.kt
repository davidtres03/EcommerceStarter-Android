package com.ecommercestarter.admin.presentation.storemanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDashboardScreen(
    onNavigateBack: () -> Unit,
    viewModel: SystemMonitoringViewModel = hiltViewModel()
) {
    val statusState by viewModel.statusState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Service Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadServiceStatus() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when (statusState) {
            is StatusState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is StatusState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text((statusState as StatusState.Error).message)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadServiceStatus() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is StatusState.Success -> {
                val status = (statusState as StatusState.Success).status
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // System Status Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (status.isWebServiceOnline && status.databaseConnected) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (status.isWebServiceOnline && status.databaseConnected) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = if (status.isWebServiceOnline && status.databaseConnected) 
                                    MaterialTheme.colorScheme.onPrimaryContainer 
                                else MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Service Status",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    if (status.isWebServiceOnline) "Online" else "Offline",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Last updated: ${status.timestamp}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // Metrics Grid
                    Text(
                        "System Metrics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricCard(
                            title = "Response Time",
                            value = "${status.responseTimeMs}ms",
                            icon = Icons.Default.Speed,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Active Users",
                            value = "${status.activeUserCount}",
                            icon = Icons.Default.People,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricCard(
                            title = "Pending Orders",
                            value = "${status.pendingOrdersCount}",
                            icon = Icons.Default.ShoppingCart,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Queue Size",
                            value = "${status.queueSize}",
                            icon = Icons.Default.Queue,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Resource Usage
                    Text(
                        "Resource Usage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            HealthMetricRow("CPU Usage", "${String.format("%.1f", status.cpuUsagePercent)}%", (status.cpuUsagePercent / 100).toFloat())
                            Divider(modifier = Modifier.padding(vertical = 12.dp))
                            HealthMetricRow("Memory Usage", "${String.format("%.1f", status.memoryUsageMb)} MB", (status.memoryUsageMb / 1024).toFloat().coerceIn(0f, 1f))
                            Divider(modifier = Modifier.padding(vertical = 12.dp))
                            HealthMetricRow("Uptime", "${String.format("%.1f", status.uptimePercent)}%", (status.uptimePercent / 100).toFloat())
                        }
                    }

                    // Service Components
                    Text(
                        "Service Components",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    ServiceInfoCard(
                        "Web Service",
                        if (status.isWebServiceOnline) "Online" else "Offline",
                        if (status.isWebServiceOnline) Icons.Default.CheckCircle else Icons.Default.Error,
                        status.isWebServiceOnline
                    )
                    ServiceInfoCard(
                        "Background Service",
                        if (status.isBackgroundServiceRunning) "Running" else "Stopped",
                        if (status.isBackgroundServiceRunning) Icons.Default.CheckCircle else Icons.Default.Error,
                        status.isBackgroundServiceRunning
                    )
                    ServiceInfoCard(
                        "Database",
                        if (status.databaseConnected) "Connected" else "Disconnected",
                        if (status.databaseConnected) Icons.Default.Storage else Icons.Default.Error,
                        status.databaseConnected
                    )

                    // Recent Errors
                    if (status.recentErrors.isNotEmpty()) {
                        Text(
                            "Recent Errors (${status.recentErrors.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )

                        status.recentErrors.take(5).forEach { error ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            error.source,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Surface(
                                            color = when(error.severity) {
                                                "Critical" -> MaterialTheme.colorScheme.error
                                                "Warning" -> MaterialTheme.colorScheme.tertiary
                                                else -> MaterialTheme.colorScheme.secondary
                                            },
                                            shape = MaterialTheme.shapes.small
                                        ) {
                                            Text(
                                                error.severity,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onError
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        error.message,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        error.timestamp,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ServiceInfoCard(
    title: String,
    status: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isHealthy: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (isHealthy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.bodyLarge)
            }
            Surface(
                color = if (isHealthy) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    status,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isHealthy) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun HealthMetricRow(name: String, value: String, progress: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
