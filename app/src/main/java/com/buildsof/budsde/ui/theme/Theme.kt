package com.buildsof.budsde.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = OrangeDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3A2E28),
    onPrimaryContainer = Color(0xFFFFDCC8),
    
    secondary = BlueDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E3A36),
    onSecondaryContainer = Color(0xFFB8E6E0),
    
    tertiary = YellowDark,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF4A3F1F),
    onTertiaryContainer = Color(0xFFFFECB3),
    
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF5D1F1A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2B2930),
    onSurfaceVariant = Color(0xFFCAC4D0),
    
    outline = GreyDark
)

private val LightColorScheme = lightColorScheme(
    primary = OrangeLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDCC8),
    onPrimaryContainer = Color(0xFF3A1300),
    
    secondary = BlueLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB8E6E0),
    onSecondaryContainer = Color(0xFF002019),
    
    tertiary = YellowLight,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFFFECB3),
    onTertiaryContainer = Color(0xFF2B1700),
    
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF3EDF7),
    onSurfaceVariant = Color(0xFF49454F),
    
    outline = GreyLight
)

@Composable
fun BuildBuddyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to use custom theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

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