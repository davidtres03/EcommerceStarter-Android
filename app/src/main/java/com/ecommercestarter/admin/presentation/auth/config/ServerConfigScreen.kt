package com.ecommercestarter.admin.presentation.auth.config

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ServerConfigScreen(
    onConfigured: () -> Unit,
    viewModel: ServerConfigViewModel = hiltViewModel()
) {
    val serverUrl by viewModel.serverUrl.collectAsState()
    val configState by viewModel.configState.collectAsState()
    
    LaunchedEffect(configState) {
        if (configState is ServerConfigState.Success) {
            onConfigured()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Configure Server",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Enter your EcommerceStarter server URL",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = serverUrl,
                onValueChange = viewModel::onServerUrlChange,
                label = { Text("Server URL") },
                placeholder = { Text("https://your-store.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = configState !is ServerConfigState.Loading,
                isError = configState is ServerConfigState.Error
            )
            
            if (configState is ServerConfigState.Error) {
                Text(
                    text = (configState as ServerConfigState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Text(
                text = "Examples:\n• https://store.example.com\n• http://192.168.1.100:5000\n• http://localhost:5000",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { viewModel.saveServerUrl() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = configState !is ServerConfigState.Loading && serverUrl.isNotBlank()
            ) {
                if (configState is ServerConfigState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continue")
                }
            }
        }
    }
}
