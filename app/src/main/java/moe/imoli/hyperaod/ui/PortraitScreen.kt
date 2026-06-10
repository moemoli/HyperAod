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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import moe.imoli.hyperaod.R
import moe.imoli.hyperaod.ui.settings.ColorInputField
import moe.imoli.hyperaod.ui.settings.DropdownField
import moe.imoli.hyperaod.ui.settings.DropdownOption
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

    // 歌词设置状态
    var switchLyric by remember { mutableStateOf(false) }
    var fontSize by remember { mutableStateOf(24f) }
    var fontColor by remember { mutableStateOf(Color.White) }
    var marginTop by remember { mutableStateOf(0f) }
    var marginBottom by remember { mutableStateOf(0f) }
    var marginLeft by remember { mutableStateOf(0f) }
    var marginRight by remember { mutableStateOf(0f) }
    var alignment by remember { mutableStateOf(1) }
    var enterAnim by remember { mutableStateOf(1) }
    var exitAnim by remember { mutableStateOf(1) }
    var enterAnimDuration by remember { mutableStateOf(300f) }
    var exitAnimDuration by remember { mutableStateOf(300f) }

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
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 内容区域，带有状态栏和水平边距
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp),
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
                label = "歌词设置",
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
                item("字体大小") {
                    SliderInputField(
                        value = fontSize,
                        onValueChange = { fontSize = it },
                        valueRange = 12f..72f,
                        label = "字体大小"
                    )
                }
                item("字体颜色") {
                    ColorInputField(
                        value = fontColor,
                        onValueChange = { fontColor = it },
                        label = "字体颜色"
                    )
                }
                item("上边距") {
                    SliderInputField(
                        value = marginTop,
                        onValueChange = { marginTop = it },
                        valueRange = 0f..500f,
                        label = "上边距"
                    )
                }
                item("下边距") {
                    SliderInputField(
                        value = marginBottom,
                        onValueChange = { marginBottom = it },
                        valueRange = 0f..500f,
                        label = "下边距"
                    )
                }
                item("左边距") {
                    SliderInputField(
                        value = marginLeft,
                        onValueChange = { marginLeft = it },
                        valueRange = 0f..500f,
                        label = "左边距"
                    )
                }
                item("右边距") {
                    SliderInputField(
                        value = marginRight,
                        onValueChange = { marginRight = it },
                        valueRange = 0f..500f,
                        label = "右边距"
                    )
                }
                item("对齐方式") {
                    DropdownField(
                        value = alignment,
                        onValueChange = { alignment = it },
                        "对齐方式",
                        options = listOf(
                            DropdownOption("左对齐", 0),
                            DropdownOption("居中", 1),
                            DropdownOption("右对齐", 2)
                        ),

                    )
                }
                item("进入动画") {
                    DropdownField(
                        value = enterAnim,
                        onValueChange = { enterAnim = it },
                        "进入动画",
                        options = listOf(
                            DropdownOption("无", 0),
                            DropdownOption("淡入", 1),
                            DropdownOption("上滑", 2),
                            DropdownOption("下滑", 3),
                            DropdownOption("左滑", 4),
                            DropdownOption("右滑", 5)
                        ),

                    )
                }
                item("离开动画") {
                    DropdownField(
                        value = exitAnim,
                        onValueChange = { exitAnim = it },
                        "离开动画",
                        options = listOf(
                            DropdownOption("无", 0),
                            DropdownOption("淡出", 1),
                            DropdownOption("上滑", 2),
                            DropdownOption("下滑", 3),
                            DropdownOption("左滑", 4),
                            DropdownOption("右滑", 5)
                        ),

                    )
                }
                item("进入动画时长") {
                    SliderInputField(
                        value = enterAnimDuration,
                        onValueChange = { enterAnimDuration = it },
                        valueRange = 0f..2000f,
                        label = "进入动画时长 (ms)"
                    )
                }
                item("离开动画时长") {
                    SliderInputField(
                        value = exitAnimDuration,
                        onValueChange = { exitAnimDuration = it },
                        valueRange = 0f..2000f,
                        label = "离开动画时长 (ms)"
                    )
                }
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
