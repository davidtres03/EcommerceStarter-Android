package com.ecommercestarter.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ecommercestarter.admin.data.preferences.UserPreferences
import com.ecommercestarter.admin.data.repository.BrandingRepository
import com.ecommercestarter.admin.presentation.navigation.NavGraph
import com.ecommercestarter.admin.ui.theme.EcommerceStarterAdminTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var brandingRepository: BrandingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Get branding configuration from cache
            val branding by brandingRepository.getCachedBranding().collectAsState(initial = null)
            
            EcommerceStarterAdminTheme(branding = branding) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        userPreferences = userPreferences
                    )
                }
            }
        }
    }
}
