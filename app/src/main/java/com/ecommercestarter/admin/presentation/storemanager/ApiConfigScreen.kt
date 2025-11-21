package com.ecommercestarter.admin.presentation.storemanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecommercestarter.admin.data.model.ApiConfiguration
import com.ecommercestarter.admin.data.model.ApiConfigGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiConfigScreen(
    onNavigateBack: () -> Unit,
    viewModel: ApiConfigViewModel = hiltViewModel()
) {
    val configState by viewModel.configState.collectAsState()
    val toggleState by viewModel.toggleState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar for toggle success/error
    LaunchedEffect(toggleState) {
        when (toggleState) {
            is ApiConfigViewModel.ToggleState.Success -> {
                val state = toggleState as ApiConfigViewModel.ToggleState.Success
                snackbarHostState.showSnackbar(
                    message = "Configuration ${if (state.newState) "enabled" else "disabled"}",
                    duration = SnackbarDuration.Short
                )
                viewModel.resetToggleState()
            }
            is ApiConfigViewModel.ToggleState.Error -> {
                val error = toggleState as ApiConfigViewModel.ToggleState.Error
                snackbarHostState.showSnackbar(
                    message = error.message,
                    duration = SnackbarDuration.Long
                )
                viewModel.resetToggleState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("API Configuration") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadConfigurations() }) {
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
        when (configState) {
            is ApiConfigViewModel.ConfigState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Loading API configurations...")
                    }
                }
            }
            
            is ApiConfigViewModel.ConfigState.Error -> {
                val error = (configState as ApiConfigViewModel.ConfigState.Error).message
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.loadConfigurations() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retry")
                        }
                    }
                }
            }
            
            is ApiConfigViewModel.ConfigState.Success -> {
                val configGroups = (configState as ApiConfigViewModel.ConfigState.Success).configGroups
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(configGroups) { group ->
                        ApiConfigGroupCard(
                            group = group,
                            onToggle = { configId -> viewModel.toggleConfiguration(configId) },
                            toggleState = toggleState
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApiConfigGroupCard(
    group: ApiConfigGroup,
    onToggle: (Int) -> Unit,
    toggleState: ApiConfigViewModel.ToggleState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Group header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getIconForApiType(group.apiType),
                    contentDescription = group.apiType,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = group.apiType,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${group.count} configuration${if (group.count != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            
            // Configurations list
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                group.configurations.forEach { config ->
                    ApiConfigItem(
                        config = config,
                        onToggle = onToggle,
                        isToggling = toggleState is ApiConfigViewModel.ToggleState.Loading &&
                                (toggleState as ApiConfigViewModel.ToggleState.Loading).configId == config.id
                    )
                }
            }
        }
    }
}

@Composable
fun ApiConfigItem(
    config: ApiConfiguration,
    onToggle: (Int) -> Unit,
    isToggling: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = config.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!config.description.isNullOrEmpty()) {
                        Text(
                            text = config.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                if (isToggling) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Switch(
                        checked = config.isActive,
                        onCheckedChange = { onToggle(config.id) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Status badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Credentials status
                Surface(
                    color = if (config.hasCredentials)
                        MaterialTheme.colorScheme.successContainer
                    else
                        MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (config.hasCredentials) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (config.hasCredentials)
                                MaterialTheme.colorScheme.onSuccessContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (config.hasCredentials) "Configured" else "Not Configured",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (config.hasCredentials)
                                MaterialTheme.colorScheme.onSuccessContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                // Test mode badge
                if (config.isTestMode) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = "TEST MODE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun getIconForApiType(apiType: String): ImageVector {
    return when (apiType.lowercase()) {
        "email" -> Icons.Default.Email
        "payment", "payments" -> Icons.Default.Payment
        "analytics" -> Icons.Default.Analytics
        "images", "image" -> Icons.Default.Image
        "shipping" -> Icons.Default.LocalShipping
        "social" -> Icons.Default.Share
        "ai" -> Icons.Default.SmartToy
        else -> Icons.Default.Api
    }
}

// Extension colors
val ColorScheme.successContainer: androidx.compose.ui.graphics.Color
    @Composable
    get() = MaterialTheme.colorScheme.primaryContainer

val ColorScheme.onSuccessContainer: androidx.compose.ui.graphics.Color
    @Composable
    get() = MaterialTheme.colorScheme.onPrimaryContainer

val ColorScheme.errorContainer: androidx.compose.ui.graphics.Color
    @Composable
    get() = MaterialTheme.colorScheme.errorContainer

