package com.smartcampus.ai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*

// ─────────────────────────────────────────────
//  COLOR PALETTE
// ─────────────────────────────────────────────
object SmartCampusColors {
    // Primary - Electric Violet
    val Primary = Color(0xFF6C63FF)
    val PrimaryVariant = Color(0xFF5A52E0)
    val OnPrimary = Color(0xFFFFFFFF)

    // Secondary - Cyan Accent
    val Secondary = Color(0xFF00D4AA)
    val OnSecondary = Color(0xFF003A2E)

    // Tertiary - Amber
    val Tertiary = Color(0xFFFFB74D)
    val OnTertiary = Color(0xFF2A1C00)

    // Dark Background
    val Background = Color(0xFF0F0F1A)
    val Surface = Color(0xFF1A1A2E)
    val SurfaceVariant = Color(0xFF252540)
    val SurfaceElevated = Color(0xFF2D2D4E)

    // Light Background
    val BackgroundLight = Color(0xFFF5F5FF)
    val SurfaceLight = Color(0xFFFFFFFF)
    val SurfaceVariantLight = Color(0xFFEEEEFF)

    // Text
    val OnBackground = Color(0xFFE8E8FF)
    val OnSurface = Color(0xFFE0E0F0)
    val OnSurfaceVariant = Color(0xFFAAABCC)

    // Error
    val Error = Color(0xFFFF5370)
    val OnError = Color(0xFFFFFFFF)

    // Priority Colors
    val PriorityLow = Color(0xFF4CAF50)
    val PriorityMedium = Color(0xFFFFB74D)
    val PriorityHigh = Color(0xFFFF7043)
    val PriorityUrgent = Color(0xFFFF5370)

    // Status Colors
    val StatusPending = Color(0xFF90CAF9)
    val StatusInProgress = Color(0xFFFFB74D)
    val StatusCompleted = Color(0xFF4CAF50)

    // Subject Colors (for timetable/attendance)
    val SubjectColors = listOf(
        Color(0xFF6C63FF), Color(0xFF00D4AA), Color(0xFFFFB74D),
        Color(0xFFFF5370), Color(0xFF40C4FF), Color(0xFFE040FB),
        Color(0xFF69F0AE), Color(0xFFFF6E40)
    )
}

// ─────────────────────────────────────────────
//  COLOR SCHEMES
// ─────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary = SmartCampusColors.Primary,
    onPrimary = SmartCampusColors.OnPrimary,
    primaryContainer = Color(0xFF2D2B6E),
    onPrimaryContainer = Color(0xFFCCCBFF),
    secondary = SmartCampusColors.Secondary,
    onSecondary = SmartCampusColors.OnSecondary,
    secondaryContainer = Color(0xFF003A2E),
    onSecondaryContainer = Color(0xFF00D4AA),
    tertiary = SmartCampusColors.Tertiary,
    onTertiary = SmartCampusColors.OnTertiary,
    background = SmartCampusColors.Background,
    onBackground = SmartCampusColors.OnBackground,
    surface = SmartCampusColors.Surface,
    onSurface = SmartCampusColors.OnSurface,
    surfaceVariant = SmartCampusColors.SurfaceVariant,
    onSurfaceVariant = SmartCampusColors.OnSurfaceVariant,
    error = SmartCampusColors.Error,
    onError = SmartCampusColors.OnError,
    outline = Color(0xFF6060AA)
)

private val LightColorScheme = lightColorScheme(
    primary = SmartCampusColors.Primary,
    onPrimary = SmartCampusColors.OnPrimary,
    primaryContainer = Color(0xFFE8E7FF),
    onPrimaryContainer = Color(0xFF1A0080),
    secondary = Color(0xFF00A882),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCF5EC),
    onSecondaryContainer = Color(0xFF003A2E),
    tertiary = Color(0xFFE65100),
    onTertiary = Color(0xFFFFFFFF),
    background = SmartCampusColors.BackgroundLight,
    onBackground = Color(0xFF1A1A2E),
    surface = SmartCampusColors.SurfaceLight,
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = SmartCampusColors.SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF44446A),
    error = SmartCampusColors.Error,
    onError = SmartCampusColors.OnError
)

// ─────────────────────────────────────────────
//  TYPOGRAPHY
// ─────────────────────────────────────────────
val SmartCampusTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    )
)

// ─────────────────────────────────────────────
//  THEME COMPOSABLE
// ─────────────────────────────────────────────
@Composable
fun SmartCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SmartCampusTypography,
        content = content
    )
}
