package com.ecommercestarter.admin.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Login Successful! í¾‰",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "This is a placeholder dashboard screen.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
