package moe.imoli.hyperaod.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import moe.imoli.hyperaod.ui.theme.HyperAodTheme

/**
 * 模块设置界面（占位）
 */
@Composable
fun ModuleSettingsScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val iconTint = MaterialTheme.colorScheme.outlineVariant

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部返回按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(20.dp)) {
                    val strokeW = 2.5.dp.toPx()
                    val cap = StrokeCap.Round
                    val cx = size.width * 0.32f
                    val cy = size.height * 0.5f
                    val r = size.width * 0.38f
                    val dx = r * 0.6428f
                    val dy = r * 0.7660f
                    drawLine(
                        color = iconTint,
                        start = Offset(cx + dx, cy - dy),
                        end = Offset(cx, cy),
                        strokeWidth = strokeW,
                        cap = cap
                    )
                    drawLine(
                        color = iconTint,
                        start = Offset(cx, cy),
                        end = Offset(cx + dx, cy + dy),
                        strokeWidth = strokeW,
                        cap = cap
                    )
                }
            }
        }

        // 居中占位内容
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "模块设置",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun ModuleSettingsScreenPreview() {
    HyperAodTheme {
        ModuleSettingsScreen()
    }
}
