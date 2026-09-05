package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF003915),
    primaryContainer = HarvestGreenDark,
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFF4E2600),
    secondaryContainer = Color(0xFF663E00),
    onSecondaryContainer = Color(0xFFFFD54F),
    background = Color(0xFF111813),
    surface = Color(0xFF162019),
    onBackground = Color(0xFFE2E8E3),
    onSurface = Color(0xFFE2E8E3)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = HarvestGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = HarvestGreenContainer,
    onPrimaryContainer = HarvestOnGreenContainer,
    secondary = HarvestAmberPrimary,
    onSecondary = Color.White,
    secondaryContainer = HarvestAmberContainer,
    onSecondaryContainer = HarvestOnAmberContainer,
    background = BentoBackground,
    surface = BentoCardWhite,
    onBackground = BentoTextPrimary,
    onSurface = BentoTextPrimary,
    surfaceVariant = BentoContainerLight,
    onSurfaceVariant = BentoTextMuted,
    outline = BentoBorder,
    outlineVariant = BentoBorder
  )

@Composable
fun HarvestHubTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our brand agricultural scheme for consistent farmer experience
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  HarvestHubTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
