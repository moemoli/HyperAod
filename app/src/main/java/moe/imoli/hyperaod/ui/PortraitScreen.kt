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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import moe.imoli.hyperaod.AodSettings
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
    onAbout: () -> Unit = {},
    onSettings: () -> Unit = {}
) {
    var showRestartDialog by remember { mutableStateOf(false) }


    // 歌词设置状态
    var switchLyric by remember { mutableStateOf(AodSettings.lyric.enable) }
    var fontSize by remember { mutableStateOf(AodSettings.lyric.fontSize) }
    var fontColor by remember { mutableStateOf(Color(AodSettings.lyric.fontColor.toInt())) }
    var marginTop by remember { mutableStateOf(AodSettings.lyric.marginTop) }
    var marginBottom by remember { mutableStateOf(AodSettings.lyric.marginBottom) }
    var marginLeft by remember { mutableStateOf(AodSettings.lyric.marginLeft) }
    var marginRight by remember { mutableStateOf(AodSettings.lyric.marginRight) }
    var alignment by remember { mutableStateOf(AodSettings.lyric.alignment.type) }
    var enterAnim by remember { mutableStateOf(AodSettings.lyric.enterAnim.type) }
    var exitAnim by remember { mutableStateOf(AodSettings.lyric.exitAnim.type) }
    var enterAnimDuration by remember { mutableStateOf(AodSettings.lyric.enterAnimDuration) }
    var exitAnimDuration by remember { mutableStateOf(AodSettings.lyric.exitAnimDuration) }

    // 设置加载完成后重新同步本地状态
    var reloadTrigger by remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val listener: () -> Unit = { reloadTrigger++ }
        AodSettings.addOnReloadedListener(listener)
        // 已加载过则立即触发一次
        if (AodSettings.loaded) reloadTrigger++
        onDispose { AodSettings.removeOnReloadedListener(listener) }
    }
    LaunchedEffect(reloadTrigger) {
        if (reloadTrigger > 0) {
            switchLyric = AodSettings.lyric.enable
            fontSize = AodSettings.lyric.fontSize
            fontColor = Color(AodSettings.lyric.fontColor.toInt())
            marginTop = AodSettings.lyric.marginTop
            marginBottom = AodSettings.lyric.marginBottom
            marginLeft = AodSettings.lyric.marginLeft
            marginRight = AodSettings.lyric.marginRight
            alignment = AodSettings.lyric.alignment.type
            enterAnim = AodSettings.lyric.enterAnim.type
            exitAnim = AodSettings.lyric.exitAnim.type
            enterAnimDuration = AodSettings.lyric.enterAnimDuration
            exitAnimDuration = AodSettings.lyric.exitAnimDuration
        }
    }

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
                    .padding(),
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
                IconButton(onClick = onSettings) {
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
                            onCheckedChange = {
                                switchLyric = it
                                AodSettings.lyric.enable = it

                            },
                            label = "歌词显示",
                            description = "在息屏上显示当前播放歌词"
                        )
                    }
                    item("字体大小") {
                        SliderInputField(
                            value = fontSize,
                            onValueChange = {
                                fontSize = it
                                AodSettings.lyric.fontSize = it

                            },
                            valueRange = 12f..72f,
                            label = "字体大小"
                        )
                    }
                    item("字体颜色") {
                        ColorInputField(
                            value = fontColor,
                            onValueChange = {
                                fontColor = it
                                AodSettings.lyric.fontColor = it.toArgb().toLong()

                            },
                            label = "字体颜色"
                        )
                    }
                    item("上边距") {
                        SliderInputField(
                            value = marginTop,
                            onValueChange = {
                                marginTop = it
                                AodSettings.lyric.marginTop = it

                            },
                            valueRange = 0f..500f,
                            label = "上边距"
                        )
                    }
                    item("下边距") {
                        SliderInputField(
                            value = marginBottom,
                            onValueChange = {
                                marginBottom = it
                                AodSettings.lyric.marginBottom = it

                            },
                            valueRange = 0f..500f,
                            label = "下边距"
                        )
                    }
                    item("左边距") {
                        SliderInputField(
                            value = marginLeft,
                            onValueChange = {
                                marginLeft = it
                                AodSettings.lyric.marginLeft = it

                            },
                            valueRange = 0f..500f,
                            label = "左边距"
                        )
                    }
                    item("右边距") {
                        SliderInputField(
                            value = marginRight,
                            onValueChange = {
                                marginRight = it
                                AodSettings.lyric.marginRight = it

                            },
                            valueRange = 0f..500f,
                            label = "右边距"
                        )
                    }
                    item("对齐方式") {
                        DropdownField(
                            value = alignment,
                            onValueChange = {
                                alignment = it
                                AodSettings.lyric.updateAlignment(it)
                            },
                            "对齐方式",
                            options = listOf(
                                DropdownOption("居中", 0),
                                DropdownOption("左对齐", 1),
                                DropdownOption("右对齐", 2)
                            ),

                            )
                    }
                    item("进入动画") {
                        DropdownField(
                            value = enterAnim,
                            onValueChange = {
                                enterAnim = it
                                AodSettings.lyric.updateEnterAnim(it)
                            },
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
                            onValueChange = {
                                exitAnim = it
                                AodSettings.lyric.updateExitAnim(it)
                            },
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
                            onValueChange = {
                                enterAnimDuration = it
                                AodSettings.lyric.enterAnimDuration = it

                            },
                            valueRange = 0f..2000f,
                            label = "进入动画时长 (ms)"
                        )
                    }
                    item("离开动画时长") {
                        SliderInputField(
                            value = exitAnimDuration,
                            onValueChange = {
                                exitAnimDuration = it
                                AodSettings.lyric.exitAnimDuration = it
                            },
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
