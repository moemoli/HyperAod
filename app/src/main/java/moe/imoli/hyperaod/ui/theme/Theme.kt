package moe.imoli.hyperaod.ui.theme

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

// ========================
// Dark Color Scheme
// ========================
private val HyperDarkColorScheme = darkColorScheme(
    // Primary: 与浅色一致
    primary = Color(0xFF3382FF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCEEFB),
    onPrimaryContainer = Color(0xFF001D36),
    // Secondary: 与浅色一致
    secondary = HyperTeal40,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = HyperTeal90,
    onSecondaryContainer = HyperTeal10,
    // Tertiary: 与浅色一致
    tertiary = HyperOrange40,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = HyperOrange90,
    onTertiaryContainer = HyperOrange10,
    // Error: 与浅色一致
    error = HyperRed40,
    onError = Color(0xFFFFFFFF),
    errorContainer = HyperRed90,
    onErrorContainer = HyperRed10,
    // Surface & Background
    background = Color(0xFF000000),
    onBackground = Color(0xFFF4F4F4),
    surface = Color(0xFF242424),
    onSurface = Color(0xFFF4F4F4),
    surfaceVariant = Color(0xFFE5E5E5),
    onSurfaceVariant = Color(0xFF929292),
    // Surface containers (设置组背景统一 #242424)
    surfaceTint = Color(0xFF3382FF),
    surfaceContainerLowest = Color(0xFF242424),
    surfaceContainerLow = Color(0xFF242424),
    surfaceContainer = Color(0xFF242424),
    surfaceContainerHigh = Color(0xFF242424),
    surfaceContainerHighest = Color(0xFF242424),
    // Outline
    outline = Color(0xFF656565),
    outlineVariant = Color(0xFFE6E6E6),
    // Inverse: 与浅色一致
    inverseSurface = Color(0xFFE6E6E6),
    inverseOnSurface = Color(0xFFF7F7F7),
    inversePrimary = HyperBlue80,
    // Scrim & Shadow
    scrim = HyperGray0,
    surfaceDim = Color(0xFF1A1A1A),
    surfaceBright = Color(0xFF242424)
)

// ========================
// HyperOS 3 Light Color Scheme
// ========================
private val HyperLightColorScheme = lightColorScheme(
    // Primary: #3382FF (Switch 开启、滑块、复选框)
    primary = Color(0xFF3382FF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCEEFB),
    onPrimaryContainer = Color(0xFF001D36),
    // Secondary
    secondary = HyperTeal40,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = HyperTeal90,
    onSecondaryContainer = HyperTeal10,
    // Tertiary
    tertiary = HyperOrange40,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = HyperOrange90,
    onTertiaryContainer = HyperOrange10,
    // Error
    error = HyperRed40,
    onError = Color(0xFFFFFFFF),
    errorContainer = HyperRed90,
    onErrorContainer = HyperRed10,
    // Surface & Background
    background = Color(0xFFF7F7F7),
    onBackground = Color(0xFF161616),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF161616),
    surfaceVariant = Color(0xFFE5E5E5),
    onSurfaceVariant = Color(0xFF666666),
    // Surface containers (设置组卡片统一白色)
    surfaceTint = Color(0xFF3382FF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFFFFFFF),
    surfaceContainerHighest = Color(0xFFFFFFFF),
    // Outline (图标、分隔线)
    outline = Color(0xFFB4B4B4),
    outlineVariant = Color(0xFF393939),
    // Inverse
    inverseSurface = Color(0xFF393939),
    inverseOnSurface = Color(0xFFF7F7F7),
    inversePrimary = HyperBlue80,
    // Scrim & Shadow
    scrim = HyperGray0,
    surfaceDim = Color(0xFFF0F0F0),
    surfaceBright = Color(0xFFFFFFFF)
)

@Composable
fun HyperAodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> HyperDarkColorScheme
        else -> HyperLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // HyperOS 风格：状态栏和导航栏使用透明背景
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
