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
    val background = backgroundColor.toComposeColor()
    val surface = surfaceColor.toComposeColor()
    
    return if (darkTheme) {
        darkColorScheme(
            primary = primary,
            secondary = secondary,
            tertiary = tertiary,
            background = background,
            surface = surface
        )
    } else {
        lightColorScheme(
            primary = primary,
            secondary = secondary,
            tertiary = tertiary,
            background = background,
            surface = surface
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
