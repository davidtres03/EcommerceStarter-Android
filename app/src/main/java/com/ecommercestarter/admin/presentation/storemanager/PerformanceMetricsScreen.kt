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
fun PerformanceMetricsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SystemMonitoringViewModel = hiltViewModel()
) {
    val metricsState by viewModel.metricsState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadPerformanceMetrics()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Metrics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadPerformanceMetrics() }) {
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
        when (metricsState) {
            is MetricsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MetricsState.Error -> {
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
                        Text((metricsState as MetricsState.Error).message)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadPerformanceMetrics() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is MetricsState.Success -> {
                val metrics = (metricsState as MetricsState.Success).metrics
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summary Cards
                    Text(
                        "Average Performance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryMetricCard(
                            title = "Response Time",
                            value = "${String.format("%.0f", metrics.averageResponseTime)} ms",
                            icon = Icons.Default.Speed,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricCard(
                            title = "Memory",
                            value = "${String.format("%.1f", metrics.averageMemoryUsage)} MB",
                            icon = Icons.Default.Memory,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryMetricCard(
                            title = "CPU Usage",
                            value = "${String.format("%.1f", metrics.averageCpuUsage)}%",
                            icon = Icons.Default.Dashboard,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricCard(
                            title = "Uptime",
                            value = "${String.format("%.1f", metrics.averageUptime)}%",
                            icon = Icons.Default.CloudDone,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // Time Period
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Data Period",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "From: ${metrics.startDate}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "To: ${metrics.endDate}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Data points: ${metrics.metrics.size}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    
                    // Recent Metrics
                    Text(
                        "Recent Data Points",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    metrics.metrics.takeLast(10).reversed().forEach { metric ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    metric.timestamp,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                MetricRow("Response Time", "${metric.responseTimeMs} ms")
                                MetricRow("Memory", "${String.format("%.1f", metric.memoryUsageMb)} MB")
                                MetricRow("CPU", "${String.format("%.1f", metric.cpuUsagePercent)}%")
                                MetricRow("Uptime", "${String.format("%.1f", metric.uptimePercent)}%")
                                MetricRow("Active Users", "${metric.activeUserCount}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
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
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
