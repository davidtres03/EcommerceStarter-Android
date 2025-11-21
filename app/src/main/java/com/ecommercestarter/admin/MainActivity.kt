package com.ecommercestarter.admin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.ecommercestarter.admin.data.preferences.UserPreferences
import com.ecommercestarter.admin.data.repository.BrandingRepository
import com.ecommercestarter.admin.presentation.navigation.NavGraph
import com.ecommercestarter.admin.ui.theme.EcommerceStarterAdminTheme
import com.ecommercestarter.admin.util.BiometricHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var brandingRepository: BrandingRepository

    @Inject
    lateinit var biometricHelper: BiometricHelper
    
    private var requiresAuth by mutableStateOf(false)
    private var isFirstLaunch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Make sure the app draws behind the system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Add lifecycle observer for background/foreground detection
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                android.util.Log.d("MainActivity", "onStop - isFirstLaunch: $isFirstLaunch")
                // App going to background - require re-authentication when it comes back
                if (!isFirstLaunch) {
                    lifecycleScope.launch {
                        val hasAuth = userPreferences.authToken.first()
                        android.util.Log.d("MainActivity", "hasAuth: ${!hasAuth.isNullOrEmpty()}")
                        if (!hasAuth.isNullOrEmpty()) {
                            requiresAuth = true
                            android.util.Log.d("MainActivity", "requiresAuth set to TRUE")
                        }
                    }
                }
            }
            
            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                android.util.Log.d("MainActivity", "onStart - isFirstLaunch: $isFirstLaunch, requiresAuth: $requiresAuth")
                isFirstLaunch = false
            }
        })
        
        setContent {
            // Get branding configuration from cache
            val branding by brandingRepository.getCachedBranding().collectAsState(initial = null)
            
            // Get dark mode preference
            val darkMode by userPreferences.darkMode.collectAsState(initial = false)
            
            EcommerceStarterAdminTheme(
                branding = branding,
                darkTheme = darkMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        userPreferences = userPreferences,
                        brandingRepository = brandingRepository,
                        biometricHelper = biometricHelper,
                        activity = this@MainActivity,
                        requiresAuth = requiresAuth,
                        onAuthComplete = { requiresAuth = false }
                    )
                }
            }
        }
    }
}
