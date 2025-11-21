package com.ecommercestarter.admin.presentation.auth.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecommercestarter.admin.data.preferences.UserPreferences
import com.ecommercestarter.admin.data.repository.BrandingRepository
import com.ecommercestarter.admin.domain.model.AuthState
import com.ecommercestarter.admin.presentation.components.DarkModeToggle
import com.ecommercestarter.admin.util.BiometricHelper
import com.ecommercestarter.admin.util.BiometricStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    userPreferences: UserPreferences,
    brandingRepository: BrandingRepository,
    biometricHelper: BiometricHelper,
    activity: FragmentActivity,
    forceAuth: Boolean = false,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val rememberMe by viewModel.rememberMe.collectAsState()
    
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Get business name from branding
    val branding by brandingRepository.getCachedBranding().collectAsState(initial = null)
    val businessName = branding?.businessName ?: "EcommerceStarter"
    
    // Fetch branding on first load if not cached
    LaunchedEffect(Unit) {
        if (branding == null) {
            brandingRepository.fetchAndCacheBranding()
        }
    }
    
    // Check if biometric is available and enabled
    val biometricStatus = remember { biometricHelper.isBiometricAvailable() }
    val biometricAvailable = biometricStatus == BiometricStatus.AVAILABLE
    val biometricEnabled by userPreferences.biometricEnabled.collectAsState(initial = false)
    val hasCredentials by userPreferences.authToken.collectAsState(initial = null)
    
    // Debug: Log biometric status
    LaunchedEffect(Unit) {
        android.util.Log.d("LoginScreen", "Biometric Status: $biometricStatus")
        android.util.Log.d("LoginScreen", "Biometric Available: $biometricAvailable")
    }
    
    var showBiometricDialog by remember { mutableStateOf(false) }
    var hasAutoPrompted by remember { mutableStateOf(false) }
    var isResumed by remember { mutableStateOf(false) }
    
    // Track activity lifecycle to know when we're actually visible
    DisposableEffect(activity) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                android.util.Log.d("LoginScreen", "Activity RESUMED")
                isResumed = true
            }
            override fun onPause(owner: LifecycleOwner) {
                android.util.Log.d("LoginScreen", "Activity PAUSED")
                isResumed = false
            }
        }
        activity.lifecycle.addObserver(observer)
        onDispose {
            activity.lifecycle.removeObserver(observer)
        }
    }
    
    // Reset auto-prompt flag when forceAuth becomes true (returning from background)
    LaunchedEffect(forceAuth) {
        if (forceAuth) {
            android.util.Log.d("LoginScreen", "Force auth triggered, resetting hasAutoPrompted")
            hasAutoPrompted = false
        }
    }
    
    // Auto-trigger biometric on screen load if enabled and has saved credentials
    // Only auto-prompt once per screen composition AND when activity is resumed
    LaunchedEffect(biometricEnabled, hasCredentials, biometricAvailable, isResumed) {
        if (!hasAutoPrompted && biometricEnabled && !hasCredentials.isNullOrEmpty() && biometricAvailable && isResumed) {
            hasAutoPrompted = true
            android.util.Log.d("LoginScreen", "Auto-prompting biometric authentication (activity is resumed)")
            // Trigger biometric authentication automatically for returning users
            if (activity != null) {
                val prompt = biometricHelper.createBiometricPrompt(
                    activity = activity,
                    onSuccess = { 
                        android.util.Log.d("LoginScreen", "Auto biometric success")
                        onLoginSuccess() 
                    },
                    onError = { error ->
                        android.util.Log.e("LoginScreen", "Auto biometric failed: $error")
                        // On biometric failure, show password form
                        scope.launch {
                            snackbarHostState.showSnackbar("Biometric failed: $error. Please use password.")
                        }
                    }
                )
                prompt.authenticate(biometricHelper.getPromptInfo("Authenticate to Continue"))
            }
        }
    }
    
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // Prompt to enable biometric if available and not already enabled
                if (biometricAvailable && !biometricEnabled) {
                    showBiometricDialog = true
                } else if (!biometricAvailable && biometricStatus != BiometricStatus.AVAILABLE) {
                    // Show why biometric isn't available
                    val message = when (biometricStatus) {
                        BiometricStatus.NOT_ENROLLED -> "No biometric enrolled on this device"
                        BiometricStatus.NO_HARDWARE -> "No biometric hardware available"
                        BiometricStatus.UNAVAILABLE -> "Biometric hardware unavailable"
                        else -> null
                    }
                    if (message != null) {
                        snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
                    }
                    onLoginSuccess()
                } else {
                    onLoginSuccess()
                }
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar(
                    message = (authState as AuthState.Error).message,
                    duration = SnackbarDuration.Long
                )
            }
            else -> {}
        }
    }
    
    // Biometric Setup Dialog
    if (showBiometricDialog) {
        AlertDialog(
            onDismissRequest = { 
                showBiometricDialog = false
                onLoginSuccess()
            },
            title = { Text("Enable Biometric Login?") },
            text = { 
                Text("Use your fingerprint or face to quickly and securely access the admin panel next time.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        android.util.Log.d("LoginScreen", "Enable button clicked")
                        android.util.Log.d("LoginScreen", "Activity: $activity")
                        // Validate biometric before enabling
                        when (activity) {
                            null -> {
                                android.util.Log.e("LoginScreen", "Activity is null!")
                                scope.launch {
                                    snackbarHostState.showSnackbar("Unable to access biometric scanner")
                                    showBiometricDialog = false
                                    onLoginSuccess()
                                }
                            }
                            else -> {
                                android.util.Log.d("LoginScreen", "Activity found, creating biometric prompt")
                                try {
                                    val prompt = biometricHelper.createBiometricPrompt(
                                        activity = activity,
                                        onSuccess = {
                                            android.util.Log.d("LoginScreen", "Biometric authentication succeeded!")
                                            scope.launch {
                                                userPreferences.setBiometricEnabled(true)
                                                showBiometricDialog = false
                                                hasAutoPrompted = true // Mark as auto-prompted to prevent double scan
                                                snackbarHostState.showSnackbar("Biometric login enabled!")
                                                onLoginSuccess()
                                            }
                                        },
                                        onError = { error ->
                                            android.util.Log.e("LoginScreen", "Biometric authentication failed: $error")
                                            scope.launch {
                                                showBiometricDialog = false
                                                // User canceled or failed - still let them in
                                                onLoginSuccess()
                                            }
                                        }
                                    )
                                    android.util.Log.d("LoginScreen", "Prompt created, getting prompt info")
                                    val promptInfo = biometricHelper.getPromptInfo("Verify Your Identity")
                                    android.util.Log.d("LoginScreen", "Calling authenticate()")
                                    prompt.authenticate(promptInfo)
                                    android.util.Log.d("LoginScreen", "authenticate() called successfully")
                                } catch (e: Exception) {
                                    android.util.Log.e("LoginScreen", "Exception during biometric setup: ${e.message}", e)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error: ${e.message}")
                                        showBiometricDialog = false
                                        onLoginSuccess()
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Text("Enable")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showBiometricDialog = false
                        onLoginSuccess()
                    }
                ) {
                    Text("Not Now")
                }
            }
        )
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Dark mode toggle in top right corner
            DarkModeToggle(
                userPreferences = userPreferences,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(8.dp)
            )
            
            // Login form centered
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "$businessName Admin",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                OutlinedTextField(
                    value = email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = authState !is AuthState.Loading
                )
                
                OutlinedTextField(
                    value = password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Password")
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.login()
                        }
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = authState !is AuthState.Loading
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = viewModel::onRememberMeChange,
                        enabled = authState !is AuthState.Loading
                    )
                    Text("Keep me signed in")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = authState !is AuthState.Loading && email.isNotBlank() && password.isNotBlank()
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Login")
                    }
                }
                
                // Biometric login button (only show if biometric is enabled)
                if (biometricEnabled && biometricAvailable && !hasCredentials.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedButton(
                        onClick = {
                            if (activity != null) {
                                val prompt = biometricHelper.createBiometricPrompt(
                                    activity = activity,
                                    onSuccess = { onLoginSuccess() },
                                    onError = { error ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar(error)
                                        }
                                    }
                                )
                                prompt.authenticate(biometricHelper.getPromptInfo("Login with Biometric"))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = authState !is AuthState.Loading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Biometric Login",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Login with Biometric")
                    }
                }
                
                if (authState is AuthState.Loading) {
                    Text(
                        text = "Connecting to server...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
