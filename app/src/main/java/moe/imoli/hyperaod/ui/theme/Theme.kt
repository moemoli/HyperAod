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
// HyperOS 3 Dark Color Scheme
// ========================
// AMOLED 友好的深色方案，符合 HyperOS 3 暗色模式设计
private val HyperDarkColorScheme = darkColorScheme(
    // Primary: HyperBlue
    primary = HyperBlue80,
    onPrimary = HyperBlue20,
    primaryContainer = HyperBlue30,
    onPrimaryContainer = HyperBlue90,
    // Secondary: HyperTeal
    secondary = HyperTeal80,
    onSecondary = HyperTeal20,
    secondaryContainer = HyperTeal30,
    onSecondaryContainer = HyperTeal90,
    // Tertiary: HyperOrange
    tertiary = HyperOrange80,
    onTertiary = HyperOrange20,
    tertiaryContainer = HyperOrange30,
    onTertiaryContainer = HyperOrange90,
    // Error
    error = HyperRed80,
    onError = HyperRed20,
    errorContainer = HyperRed30,
    onErrorContainer = HyperRed90,
    // Surface & Background (HyperOS 深色层次)
    background = HyperGray6,
    onBackground = HyperGray90,
    surface = HyperGray6,
    onSurface = HyperGray90,
    surfaceVariant = HyperBlueGray30,
    onSurfaceVariant = HyperBlueGray80,
    // Surface containers (HyperOS 层叠卡片)
    surfaceTint = HyperBlue80,
    surfaceContainerLowest = HyperGray4,
    surfaceContainerLow = HyperGray10,
    surfaceContainer = HyperGray12,
    surfaceContainerHigh = HyperGray17,
    surfaceContainerHighest = HyperGray22,
    // Outline
    outline = HyperBlueGray60,
    outlineVariant = HyperBlueGray30,
    // Inverse
    inverseSurface = HyperGray90,
    inverseOnSurface = HyperGray20,
    inversePrimary = HyperBlue40,
    // Scrim & Shadow
    scrim = HyperGray0,
    surfaceDim = HyperGray6,
    surfaceBright = HyperGray25
)

// ========================
// HyperOS 3 Light Color Scheme
// ========================
// 清爽明亮的浅色方案，符合 HyperOS 3 亮色模式设计
private val HyperLightColorScheme = lightColorScheme(
    // Primary: HyperBlue
    primary = HyperBlue40,
    onPrimary = HyperGray100,
    primaryContainer = HyperBlue90,
    onPrimaryContainer = HyperBlue10,
    // Secondary: HyperTeal
    secondary = HyperTeal40,
    onSecondary = HyperGray100,
    secondaryContainer = HyperTeal90,
    onSecondaryContainer = HyperTeal10,
    // Tertiary: HyperOrange
    tertiary = HyperOrange40,
    onTertiary = HyperGray100,
    tertiaryContainer = HyperOrange90,
    onTertiaryContainer = HyperOrange10,
    // Error
    error = HyperRed40,
    onError = HyperGray100,
    errorContainer = HyperRed90,
    onErrorContainer = HyperRed10,
    // Surface & Background (HyperOS 浅色层次)
    background = HyperGray98,
    onBackground = HyperGray10,
    surface = HyperGray98,
    onSurface = HyperGray10,
    surfaceVariant = HyperBlueGray90,
    onSurfaceVariant = HyperBlueGray30,
    // Surface containers (HyperOS 层叠卡片)
    surfaceTint = HyperBlue40,
    surfaceContainerLowest = HyperGray100,
    surfaceContainerLow = HyperGray96,
    surfaceContainer = HyperGray94,
    surfaceContainerHigh = HyperGray92,
    surfaceContainerHighest = HyperGray90,
    // Outline
    outline = HyperBlueGray50,
    outlineVariant = HyperBlueGray80,
    // Inverse
    inverseSurface = HyperGray20,
    inverseOnSurface = HyperGray95,
    inversePrimary = HyperBlue80,
    // Scrim & Shadow
    scrim = HyperGray0,
    surfaceDim = HyperGray87,
    surfaceBright = HyperGray98
)

@Composable
fun HyperAodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
