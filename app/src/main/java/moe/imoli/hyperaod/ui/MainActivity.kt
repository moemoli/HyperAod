package moe.imoli.hyperaod.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import moe.imoli.hyperaod.ModuleMain
import moe.imoli.hyperaod.ui.theme.HyperAodTheme
import java.io.DataOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HyperAodTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val configuration = LocalConfiguration.current
                    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

                    if (isLandscape) {
                        LandscapeScreen(
                            modifier = Modifier.padding(innerPadding),
                            onRestartSystemUI = { restartSystemUI() }
                        )
                    } else {
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = NavRoutes.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(NavRoutes.Home.route) {
                                PortraitScreen(
                                    onRestartSystemUI = { restartSystemUI() },
                                    onAbout = { navController.navigate(NavRoutes.About.route) },
                                    onSettings = { navController.navigate(NavRoutes.ModuleSettings.route) }
                                )
                            }
                            composable(NavRoutes.About.route) {
                                AboutScreen(onBack = { navController.popBackStack() })
                            }
                            composable(NavRoutes.ModuleSettings.route) {
                                ModuleSettingsScreen(onBack = { navController.popBackStack() })
                            }
                        }
                    }
                }
            }
        }
    }

    private fun restartSystemUI() {
        try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            os.writeBytes("killall com.android.systemui\n")
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor()
        } catch (e: Exception) {
            Log.e(ModuleMain.TAG, "Failed to restart SystemUI (non-root environment?)", e)
        }
    }
}