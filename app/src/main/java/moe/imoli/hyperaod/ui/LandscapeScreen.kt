package moe.imoli.hyperaod.ui

import android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.imoli.hyperaod.ui.theme.HyperAodTheme

/**
 * 横屏界面
 *
 * 左侧 1/3：设置面板（无关于按钮）
 * 右侧 2/3：关于界面（无返回按钮）
 */
@Composable
fun LandscapeScreen(
    modifier: Modifier = Modifier,
    onRestartSystemUI: () -> Unit = {}
) {
    Row(modifier = modifier.fillMaxSize()) {
        // 左侧 1/3：设置面板（可滑动）
        PortraitScreen(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 8.dp),
            showAboutButton = false,
            onRestartSystemUI = onRestartSystemUI
        )

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
    widthDp = 800,
    heightDp = 480,
)
@Composable
private fun LandscapeScreenPreview() {
    HyperAodTheme {
        LandscapeScreen()
    }
}
