package com.ecommercestarter.admin.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.ecommercestarter.admin.data.model.BrandingConfig
import com.ecommercestarter.admin.data.repository.BrandingRepository

/**
 * Parse hex color string to Compose Color
 * Supports #RGB, #RRGGBB, #AARRGGBB formats
 */
fun String.toComposeColor(): Color {
    var hex = this.trim().removePrefix("#")
    
    // Handle short format #RGB -> #RRGGBB
    if (hex.length == 3) {
        hex = hex.map { "$it$it" }.joinToString("")
    }
    
    // Add alpha if missing
    if (hex.length == 6) {
        hex = "FF$hex"
    }
    
    return try {
        val colorLong = hex.toLong(16)
        Color(colorLong)
    } catch (e: Exception) {
        // Fallback to purple if parsing fails
        Color(0xFF6200EE)
    }
}

/**
 * Create ColorScheme from branding configuration
 */
fun BrandingConfig.toColorScheme(darkTheme: Boolean = false): ColorScheme {
    val primary = primaryColor.toComposeColor()
    val secondary = secondaryColor.toComposeColor()
    val tertiary = accentColor.toComposeColor()
    
    return if (darkTheme) {
        darkColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primary.copy(alpha = 0.3f),
            onPrimaryContainer = Color.White,
            
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondary.copy(alpha = 0.3f),
            onSecondaryContainer = Color.White,
            
            tertiary = tertiary,
            onTertiary = Color.White,
            tertiaryContainer = tertiary.copy(alpha = 0.3f),
            onTertiaryContainer = Color.White,
            
            background = Color(0xFF1C1B1F),
            onBackground = Color(0xFFE6E1E5),
            
            surface = Color(0xFF2B2930),
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = Color(0xFF49454F),
            onSurfaceVariant = Color(0xFFCAC4D0),
            
            outline = Color(0xFF938F99)
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primary.copy(alpha = 0.15f),
            onPrimaryContainer = primary.copy(red = primary.red * 0.3f, green = primary.green * 0.3f, blue = primary.blue * 0.3f),
            
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondary.copy(alpha = 0.15f),
            onSecondaryContainer = secondary.copy(red = secondary.red * 0.3f, green = secondary.green * 0.3f, blue = secondary.blue * 0.3f),
            
            tertiary = tertiary,
            onTertiary = Color.White,
            tertiaryContainer = tertiary.copy(alpha = 0.15f),
            onTertiaryContainer = tertiary.copy(red = tertiary.red * 0.3f, green = tertiary.green * 0.3f, blue = tertiary.blue * 0.3f),
            
            background = Color(0xFFFCFCFC),
            onBackground = Color(0xFF1C1B1F),
            
            surface = Color.White,
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = Color(0xFFE7E0EC),
            onSurfaceVariant = Color(0xFF49454F),
            
            outline = Color(0xFF79747E)
        )
    }
}

/**
 * Get default branding when server branding is not available
 */
fun getDefaultBranding(): BrandingConfig {
    return BrandingConfig(
        businessName = "EcommerceStarter",
        logoUrl = "",
        primaryColor = "#6200EE",
        secondaryColor = "#03DAC6",
        accentColor = "#FF6B6B",
        backgroundColor = "#FFFFFF",
        surfaceColor = "#F5F5F5",
        textPrimaryColor = "#000000",
        textSecondaryColor = "#757575",
        faviconUrl = "",
        supportEmail = "",
        supportPhone = ""
    )
}

/**
 * Helper composable to get current branding config
 */
@Composable
fun rememberBranding(brandingRepository: BrandingRepository): BrandingConfig {
    val cachedBranding by brandingRepository.getCachedBranding().collectAsState(initial = null)
    return cachedBranding ?: getDefaultBranding()
}
