package com.ecommercestarter.admin.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ecommercestarter.admin.data.preferences.UserPreferences
import kotlinx.coroutines.launch

@Composable
fun DarkModeToggle(
    userPreferences: UserPreferences,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val darkMode by userPreferences.darkMode.collectAsState(initial = false)

    IconButton(
        onClick = {
            scope.launch {
                userPreferences.setDarkMode(!darkMode)
            }
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = if (darkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = if (darkMode) "Switch to light mode" else "Switch to dark mode",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
