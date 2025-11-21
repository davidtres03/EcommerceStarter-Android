package com.ecommercestarter.admin.presentation.storemanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecommercestarter.admin.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    val settingsState by viewModel.settingsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadSettings() }) {
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
        when (settingsState) {
            is SecurityViewModel.SettingsState.Loading -> {
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
                        Text("Loading security settings...")
                    }
                }
            }
            
            is SecurityViewModel.SettingsState.Error -> {
                val error = (settingsState as SecurityViewModel.SettingsState.Error).message
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
                        Button(onClick = { viewModel.loadSettings() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Retry")
                        }
                    }
                }
            }
            
            is SecurityViewModel.SettingsState.Success -> {
                val settings = (settingsState as SecurityViewModel.SettingsState.Success).settings
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        RateLimitingCard(settings.rateLimiting)
                    }
                    
                    item {
                        IpBlockingCard(settings.ipBlocking)
                    }
                    
                    item {
                        AccountLockoutCard(settings.accountLockout)
                    }
                    
                    item {
                        AuditLoggingCard(settings.auditLogging)
                    }
                    
                    item {
                        NotificationsCard(settings.notifications)
                    }
                    
                    item {
                        AdvancedCard(settings.advanced)
                    }
                    
                    if (settings.lastModified != null) {
                        item {
                            LastModifiedCard(settings.lastModified, settings.lastModifiedBy)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RateLimitingCard(rateLimiting: RateLimitingSettings) {
    SecurityCard(
        title = "Rate Limiting",
        icon = Icons.Default.Speed,
        enabled = rateLimiting.enabled
    ) {
        SecurityRow("Max Requests/Min (Guest)", "${rateLimiting.maxRequestsPerMinute}")
        SecurityRow("Max Requests/Sec (Guest)", "${rateLimiting.maxRequestsPerSecond}")
        SecurityRow("Max Requests/Min (Auth)", "${rateLimiting.maxRequestsPerMinuteAuth}")
        SecurityRow("Max Requests/Sec (Auth)", "${rateLimiting.maxRequestsPerSecondAuth}")
        SecurityRow(
            "Exempt Admins",
            if (rateLimiting.exemptAdmins) "Yes" else "No",
            iconType = if (rateLimiting.exemptAdmins) SecurityRowIcon.CHECK else SecurityRowIcon.CLOSE
        )
    }
}

@Composable
fun IpBlockingCard(ipBlocking: IpBlockingSettings) {
    SecurityCard(
        title = "IP Blocking",
        icon = Icons.Default.Block,
        enabled = ipBlocking.enabled
    ) {
        SecurityRow("Max Failed Login Attempts", "${ipBlocking.maxFailedLoginAttempts}")
        SecurityRow("Failed Login Window", "${ipBlocking.failedLoginWindowMinutes} minutes")
        SecurityRow("Block Duration", "${ipBlocking.blockDurationMinutes} minutes")
    }
}

@Composable
fun AccountLockoutCard(accountLockout: AccountLockoutSettings) {
    SecurityCard(
        title = "Account Lockout",
        icon = Icons.Default.Lock,
        enabled = accountLockout.enabled
    ) {
        SecurityRow("Max Attempts", "${accountLockout.maxAttempts}")
        SecurityRow("Lockout Duration", "${accountLockout.durationMinutes} minutes")
    }
}

@Composable
fun AuditLoggingCard(auditLogging: AuditLoggingSettings) {
    SecurityCard(
        title = "Audit Logging",
        icon = Icons.Default.History,
        enabled = auditLogging.enabled
    ) {
        SecurityRow("Retention Period", "${auditLogging.retentionDays} days")
    }
}

@Composable
fun NotificationsCard(notifications: NotificationSettings) {
    SecurityCard(
        title = "Security Notifications",
        icon = Icons.Default.Notifications,
        enabled = notifications.notifyOnCriticalEvents || notifications.notifyOnIpBlocking
    ) {
        SecurityRow(
            "Critical Events",
            if (notifications.notifyOnCriticalEvents) "Enabled" else "Disabled",
            iconType = if (notifications.notifyOnCriticalEvents) SecurityRowIcon.CHECK else SecurityRowIcon.CLOSE
        )
        SecurityRow(
            "IP Blocking Events",
            if (notifications.notifyOnIpBlocking) "Enabled" else "Disabled",
            iconType = if (notifications.notifyOnIpBlocking) SecurityRowIcon.CHECK else SecurityRowIcon.CLOSE
        )
        if (!notifications.notificationEmail.isNullOrEmpty()) {
            SecurityRow("Email", notifications.notificationEmail, iconType = SecurityRowIcon.EMAIL)
        }
    }
}

@Composable
fun AdvancedCard(advanced: AdvancedSettings) {
    SecurityCard(
        title = "Advanced Security",
        icon = Icons.Default.Security,
        enabled = advanced.enableGeoIpBlocking
    ) {
        SecurityRow(
            "Geo-IP Blocking",
            if (advanced.enableGeoIpBlocking) "Enabled" else "Disabled",
            iconType = if (advanced.enableGeoIpBlocking) SecurityRowIcon.CHECK else SecurityRowIcon.CLOSE
        )
        
        if (!advanced.blockedCountries.isNullOrEmpty()) {
            val countries = advanced.blockedCountries.split(',').size
            SecurityRow("Blocked Countries", "$countries configured")
        }
        
        if (!advanced.whitelistedIps.isNullOrEmpty()) {
            val ips = advanced.whitelistedIps.split(',').size
            SecurityRow("Whitelisted IPs", "$ips configured", iconType = SecurityRowIcon.CHECK)
        }
        
        if (!advanced.blacklistedIps.isNullOrEmpty()) {
            val ips = advanced.blacklistedIps.split(',').size
            SecurityRow("Blacklisted IPs", "$ips configured", iconType = SecurityRowIcon.CLOSE)
        }
    }
}

@Composable
fun LastModifiedCard(lastModified: String, lastModifiedBy: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Update,
                contentDescription = "Last Updated",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = "Last Updated: $lastModified",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!lastModifiedBy.isNullOrEmpty()) {
                    Text(
                        text = "By: $lastModifiedBy",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SecurityCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    content: @Composable ColumnScope.() -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    color = if (enabled)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (enabled) "ENABLED" else "DISABLED",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (enabled)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            
            content()
        }
    }
}

enum class SecurityRowIcon {
    NONE, CHECK, CLOSE, EMAIL
}

@Composable
fun SecurityRow(
    label: String,
    value: String,
    iconType: SecurityRowIcon = SecurityRowIcon.NONE
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                when (iconType) {
                    SecurityRowIcon.CHECK -> Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    SecurityRowIcon.CLOSE -> Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    SecurityRowIcon.EMAIL -> Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    SecurityRowIcon.NONE -> {}
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    Spacer(Modifier.height(8.dp))
}

