package moe.imoli.hyperaod.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import moe.imoli.hyperaod.R
import moe.imoli.hyperaod.ui.settings.SearchableGroup
import moe.imoli.hyperaod.ui.settings.SettingsGroup
import moe.imoli.hyperaod.ui.settings.SliderInputField
import moe.imoli.hyperaod.ui.settings.SwitchField
import moe.imoli.hyperaod.ui.theme.HyperAodTheme

/**
 * 竖屏主界面
 */
@Composable
fun PortraitScreen(
    modifier: Modifier = Modifier,
    showAboutButton: Boolean = true,
    onRestartSystemUI: () -> Unit = {},
    onAbout: () -> Unit = {}
) {
    var showRestartDialog by remember { mutableStateOf(false) }

    var switchLyric by remember { mutableStateOf(true) }
    var switchCover by remember { mutableStateOf(false) }
    var switchProgress by remember { mutableStateOf(true) }
    var switchAnimation by remember { mutableStateOf(false) }
    var fontSize by remember { mutableStateOf(24f) }
    var alpha by remember { mutableStateOf(0.8f) }

    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            title = { Text("重启系统界面") },
            text = { Text("确定要重启系统界面吗？这会短暂中断当前界面显示。") },
            confirmButton = {
                TextButton(onClick = {
                    showRestartDialog = false
                    onRestartSystemUI()
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestartDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 顶部：右侧图标按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            val iconTint = MaterialTheme.colorScheme.onSurfaceVariant
            IconButton(onClick = { showRestartDialog = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_restart),
                    contentDescription = "重启系统界面",
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = { /* TODO: 设置 */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_setting),
                    contentDescription = "设置",
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            if (showAboutButton) {
                IconButton(onClick = onAbout) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_about),
                        contentDescription = "关于",
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 标题
        Text(
            text = "HyperAod",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 搜索组件 + 设置分组
        SearchableGroup { query ->
            SettingsGroup(
                label = "显示设置",
                searchQuery = query
            ) {
                item("歌词显示") {
                    SwitchField(
                        checked = switchLyric,
                        onCheckedChange = { switchLyric = it },
                        label = "歌词显示",
                        description = "在息屏上显示当前播放歌词"
                    )
                }
                item("封面显示") {
                    SwitchField(
                        checked = switchCover,
                        onCheckedChange = { switchCover = it },
                        label = "封面显示",
                        description = "在息屏上显示专辑封面"
                    )
                }
                item("进度条") {
                    SwitchField(
                        checked = switchProgress,
                        onCheckedChange = { switchProgress = it },
                        label = "进度条",
                        description = "在息屏上显示播放进度"
                    )
                }
                item("动画效果") {
                    SwitchField(
                        checked = switchAnimation,
                        onCheckedChange = { switchAnimation = it },
                        label = "动画效果",
                        description = "歌词切换时的过渡动画"
                    )
                }
            }

            SettingsGroup(
                label = "样式设置",
                searchQuery = query
            ) {
                item("歌词字号") {
                    SliderInputField(
                        value = fontSize,
                        onValueChange = { fontSize = it },
                        valueRange = 12f..48f,
                        label = "歌词字号"
                    )
                }
                item("透明度") {
                    SliderInputField(
                        value = alpha,
                        onValueChange = { alpha = it },
                        valueRange = 0f..1f,
                        label = "透明度",
                        decimals = 2
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PortraitScreenPreview() {
    HyperAodTheme {
        PortraitScreen()
    }
}
