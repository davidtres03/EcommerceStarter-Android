package com.ecommercestarter.admin.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.ecommercestarter.admin.data.model.BrandingConfig

// Professional neutral colors for dark mode before branding is loaded
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),           // Softer blue
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFE3F2FD),
    
    secondary = Color(0xFF90A4AE),         // Refined gray-blue
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF455A64),
    onSecondaryContainer = Color(0xFFCFD8DC),
    
    tertiary = Color(0xFF81C784),          // Soft green
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF2E7D32),
    onTertiaryContainer = Color(0xFFC8E6C9),
    
    background = Color(0xFF1C1B1F),        // Slightly warmer dark
    onBackground = Color(0xFFE6E1E5),
    
    surface = Color(0xFF2B2930),           // Elevated surface
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    
    error = Color(0xFFEF5350),
    onError = Color(0xFF000000),
    errorContainer = Color(0xFFC62828),
    onErrorContainer = Color(0xFFFFEBEE)
)

// Professional light mode colors
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),           // Professional blue
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = Color(0xFF0D47A1),
    
    secondary = Color(0xFF546E7A),         // Refined gray-blue
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCFD8DC),
    onSecondaryContainer = Color(0xFF263238),
    
    tertiary = Color(0xFF388E3C),          // Professional green
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC8E6C9),
    onTertiaryContainer = Color(0xFF1B5E20),
    
    background = Color(0xFFFCFCFC),
    onBackground = Color(0xFF1C1B1F),
    
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    
    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFC62828)
)

@Composable
fun EcommerceStarterAdminTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to use server branding
    branding: BrandingConfig? = null, // Server branding config
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Use server branding if available
        branding != null -> branding.toColorScheme(darkTheme)
        
        // Fallback to dynamic color on Android 12+
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // Fallback to static theme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
