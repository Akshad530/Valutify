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
    primary = AccentGreen,
    secondary = AccentSky,
    tertiary = AccentPeach,
    background = Color(0xFF111714),
    surface = Color(0xFF19221D),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFFE2EBE6),
    onSurface = Color(0xFFEFF5F2)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryIndigo,
    secondary = AccentSky,
    tertiary = AccentGreen,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = PrimaryText,
    onSurface = PrimaryText,
    surfaceVariant = CardMintBg,
    onSurfaceVariant = CardMintText
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Force Light theme by default as requested by user
  dynamicColor: Boolean = false, // Use our gorgeous branded colors instead of OS dynamic colors
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
