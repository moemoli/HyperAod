package moe.imoli.hyperaod.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import moe.imoli.hyperaod.ui.theme.HyperAodTheme

/**
 * 横屏界面
 *
 * 左侧 1/3：设置面板（无关于按钮，支持跳转模块设置）
 * 右侧 2/3：关于界面（无返回按钮）
 */
@Composable
fun LandscapeScreen(
    modifier: Modifier = Modifier,
    onRestartSystemUI: () -> Unit = {}
) {
    Row(modifier = modifier.fillMaxSize()) {
        // 左侧 1/3：设置面板（可滑动）
        val leftNavController = rememberNavController()
        NavHost(
            navController = leftNavController,
            startDestination = NavRoutes.Home.route,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 8.dp)
        ) {
            composable(NavRoutes.Home.route) {
                PortraitScreen(
                    showAboutButton = false,
                    onRestartSystemUI = onRestartSystemUI,
                    onSettings = { leftNavController.navigate(NavRoutes.ModuleSettings.route) }
                )
            }
            composable(NavRoutes.ModuleSettings.route) {
                ModuleSettingsScreen(onBack = { leftNavController.popBackStack() })
            }
        }

        // 右侧 2/3：关于界面
        AboutScreen(
            modifier = Modifier.weight(2f),
            showBackButton = false
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    //showSystemUi = true,
    widthDp = 1200,
    heightDp = 680,
)
@Composable
private fun LandscapeScreenPreview() {
    HyperAodTheme {
        LandscapeScreen()
    }
}
