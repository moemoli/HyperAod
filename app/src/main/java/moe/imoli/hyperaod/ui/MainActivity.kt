package moe.imoli.hyperaod.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
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
                        var showAbout by remember { mutableStateOf(false) }
                        if (showAbout) {
                            AboutScreen(onBack = { showAbout = false })
                        } else {
                            PortraitScreen(
                                modifier = Modifier.padding(innerPadding),
                                onRestartSystemUI = { restartSystemUI() },
                                onAbout = { showAbout = true }
                            )
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
        } catch (_: Exception) {
            // 非 root 环境静默失败
        }
    }
}
