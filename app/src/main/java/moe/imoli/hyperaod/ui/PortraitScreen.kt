package moe.imoli.hyperaod.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import moe.imoli.hyperaod.ui.settings.DragSortField
import moe.imoli.hyperaod.ui.settings.CheckboxGroup
import moe.imoli.hyperaod.ui.settings.CheckboxItem
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

    // 一言设置状态
    var switchHitokoto by remember { mutableStateOf(AodSettings.hitokoto.enable) }
    var hitokotoFontSize by remember { mutableStateOf(AodSettings.hitokoto.fontSize) }
    var hitokotoFontColor by remember { mutableStateOf(Color(AodSettings.hitokoto.fontColor.toInt())) }
    var hitokotoMarginTop by remember { mutableStateOf(AodSettings.hitokoto.marginTop) }
    var hitokotoMarginBottom by remember { mutableStateOf(AodSettings.hitokoto.marginBottom) }
    var hitokotoMarginLeft by remember { mutableStateOf(AodSettings.hitokoto.marginLeft) }
    var hitokotoMarginRight by remember { mutableStateOf(AodSettings.hitokoto.marginRight) }
    var hitokotoAlignment by remember { mutableStateOf(AodSettings.hitokoto.alignment.type) }
    var hitokotoEnterAnim by remember { mutableStateOf(AodSettings.hitokoto.enterAnim.type) }
    var hitokotoEnterAnimDuration by remember { mutableStateOf(AodSettings.hitokoto.enterAnimDuration) }
    var hitokotoSentenceTypes by remember { mutableStateOf(AodSettings.hitokoto.sentenceTypes.toSet()) }
    var hitokotoMinLength by remember { mutableStateOf(AodSettings.hitokoto.minLength) }
    var hitokotoMaxLength by remember { mutableStateOf(AodSettings.hitokoto.maxLength) }
    var hitokotoExitAnim by remember { mutableStateOf(AodSettings.hitokoto.exitAnim.type) }
    var hitokotoExitAnimDuration by remember { mutableStateOf(AodSettings.hitokoto.exitAnimDuration) }
    var hitokotoUpdateInterval by remember { mutableStateOf(AodSettings.hitokoto.updateInterval) }

    // 行为设置状态
    var hideHitokotoWhenLyric by remember { mutableStateOf(AodSettings.behavior.hideHitokotoWhenLyric) }
    var displayOrder by remember { mutableStateOf(AodSettings.behavior.displayOrder.toList()) }

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
            switchHitokoto = AodSettings.hitokoto.enable
            hitokotoFontSize = AodSettings.hitokoto.fontSize
            hitokotoFontColor = Color(AodSettings.hitokoto.fontColor.toInt())
            hitokotoMarginTop = AodSettings.hitokoto.marginTop
            hitokotoMarginBottom = AodSettings.hitokoto.marginBottom
            hitokotoMarginLeft = AodSettings.hitokoto.marginLeft
            hitokotoMarginRight = AodSettings.hitokoto.marginRight
            hitokotoAlignment = AodSettings.hitokoto.alignment.type
            hitokotoEnterAnim = AodSettings.hitokoto.enterAnim.type
            hitokotoEnterAnimDuration = AodSettings.hitokoto.enterAnimDuration
            hitokotoSentenceTypes = AodSettings.hitokoto.sentenceTypes.toSet()
            hitokotoMinLength = AodSettings.hitokoto.minLength
            hitokotoMaxLength = AodSettings.hitokoto.maxLength
            hitokotoExitAnim = AodSettings.hitokoto.exitAnim.type
            hitokotoExitAnimDuration = AodSettings.hitokoto.exitAnimDuration
            hitokotoUpdateInterval = AodSettings.hitokoto.updateInterval
            hideHitokotoWhenLyric = AodSettings.behavior.hideHitokotoWhenLyric
            displayOrder = AodSettings.behavior.displayOrder.toList()
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
        // 内容区域（系统栏内边距 + 水平内边距）
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.systemBars.asPaddingValues())
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
                    searchQuery = query,
                    defaultExpanded = false
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

                // 一言设置分组
                SettingsGroup(
                    label = "一言设置",
                    searchQuery = query,
                    defaultExpanded = false
                ) {
                    item("一言显示") {
                        SwitchField(
                            checked = switchHitokoto,
                            onCheckedChange = {
                                switchHitokoto = it
                                AodSettings.hitokoto.enable = it
                            },
                            label = "一言显示",
                            description = "在息屏上显示一言（来自 hitokoto.cn）"
                        )
                    }
                    item("句子类型") {
                        val sentenceTypeOptions = listOf(
                            "a" to "动画",
                            "b" to "漫画",
                            "c" to "游戏",
                            "d" to "文学",
                            "e" to "原创",
                            "f" to "来自网络",
                            "g" to "其他",
                            "h" to "影视",
                            "i" to "诗词",
                            "j" to "网易云",
                            "k" to "哲学",
                            "l" to "抖"
                        )
                        val allChecked = hitokotoSentenceTypes.isEmpty() ||
                                hitokotoSentenceTypes.size == sentenceTypeOptions.size
                        CheckboxGroup(
                            label = "句子类型",
                            allChecked = allChecked,
                            onSelectAll = { select ->
                                val newTypes = if (select) {
                                    sentenceTypeOptions.map { it.first }.toMutableSet()
                                } else {
                                    mutableSetOf()
                                }
                                hitokotoSentenceTypes = newTypes
                                AodSettings.hitokoto.sentenceTypes = newTypes.toMutableSet()
                            }
                        ) {
                            sentenceTypeOptions.forEach { (code, label) ->
                                CheckboxItem(
                                    checked = hitokotoSentenceTypes.contains(code) || hitokotoSentenceTypes.isEmpty(),
                                    onCheckedChange = { checked ->
                                        val newTypes = hitokotoSentenceTypes.toMutableSet()
                                        if (hitokotoSentenceTypes.isEmpty()) {
                                            // 从"全选"状态开始，取消勾选时先填入所有再移除
                                            newTypes.addAll(sentenceTypeOptions.map { it.first })
                                        }
                                        if (checked) newTypes.add(code) else newTypes.remove(code)
                                        hitokotoSentenceTypes = newTypes
                                        AodSettings.hitokoto.sentenceTypes = newTypes.toMutableSet()
                                    },
                                    label = label
                                )
                            }
                        }
                    }
                    item("最小字数") {
                        SliderInputField(
                            value = hitokotoMinLength.toFloat(),
                            onValueChange = {
                                hitokotoMinLength = it.toInt()
                                AodSettings.hitokoto.minLength = it.toInt()
                            },
                            valueRange = 0f..100f,
                            label = "最小字数（0=不限）"
                        )
                    }
                    item("最大字数") {
                        SliderInputField(
                            value = hitokotoMaxLength.toFloat(),
                            onValueChange = {
                                hitokotoMaxLength = it.toInt()
                                AodSettings.hitokoto.maxLength = it.toInt()
                            },
                            valueRange = 0f..200f,
                            label = "最大字数（0=不限）"
                        )
                    }
                    item("字体大小") {
                        SliderInputField(
                            value = hitokotoFontSize,
                            onValueChange = {
                                hitokotoFontSize = it
                                AodSettings.hitokoto.fontSize = it
                            },
                            valueRange = 12f..72f,
                            label = "字体大小"
                        )
                    }
                    item("字体颜色") {
                        ColorInputField(
                            value = hitokotoFontColor,
                            onValueChange = {
                                hitokotoFontColor = it
                                AodSettings.hitokoto.fontColor = it.toArgb().toLong()
                            },
                            label = "字体颜色"
                        )
                    }
                    item("上边距") {
                        SliderInputField(
                            value = hitokotoMarginTop,
                            onValueChange = {
                                hitokotoMarginTop = it
                                AodSettings.hitokoto.marginTop = it
                            },
                            valueRange = 0f..500f,
                            label = "上边距"
                        )
                    }
                    item("下边距") {
                        SliderInputField(
                            value = hitokotoMarginBottom,
                            onValueChange = {
                                hitokotoMarginBottom = it
                                AodSettings.hitokoto.marginBottom = it
                            },
                            valueRange = 0f..500f,
                            label = "下边距"
                        )
                    }
                    item("左边距") {
                        SliderInputField(
                            value = hitokotoMarginLeft,
                            onValueChange = {
                                hitokotoMarginLeft = it
                                AodSettings.hitokoto.marginLeft = it
                            },
                            valueRange = 0f..500f,
                            label = "左边距"
                        )
                    }
                    item("右边距") {
                        SliderInputField(
                            value = hitokotoMarginRight,
                            onValueChange = {
                                hitokotoMarginRight = it
                                AodSettings.hitokoto.marginRight = it
                            },
                            valueRange = 0f..500f,
                            label = "右边距"
                        )
                    }
                    item("对齐方式") {
                        DropdownField(
                            value = hitokotoAlignment,
                            onValueChange = {
                                hitokotoAlignment = it
                                AodSettings.hitokoto.updateAlignment(it)
                            },
                            "对齐方式",
                            options = listOf(
                                DropdownOption("居中", 0),
                                DropdownOption("左对齐", 1),
                                DropdownOption("右对齐", 2)
                            )
                        )
                    }
                    item("进入动画") {
                        DropdownField(
                            value = hitokotoEnterAnim,
                            onValueChange = {
                                hitokotoEnterAnim = it
                                AodSettings.hitokoto.updateEnterAnim(it)
                            },
                            "进入动画",
                            options = listOf(
                                DropdownOption("无", 0),
                                DropdownOption("淡入", 1),
                                DropdownOption("上滑", 2),
                                DropdownOption("下滑", 3),
                                DropdownOption("左滑", 4),
                                DropdownOption("右滑", 5)
                            )
                        )
                    }
                    item("进入动画时长") {
                        SliderInputField(
                            value = hitokotoEnterAnimDuration,
                            onValueChange = {
                                hitokotoEnterAnimDuration = it
                                AodSettings.hitokoto.enterAnimDuration = it
                            },
                            valueRange = 0f..2000f,
                            label = "进入动画时长 (ms)"
                        )
                    }
                    item("退出动画") {
                        DropdownField(
                            value = hitokotoExitAnim,
                            onValueChange = {
                                hitokotoExitAnim = it
                                AodSettings.hitokoto.updateExitAnim(it)
                            },
                            "退出动画",
                            options = listOf(
                                DropdownOption("无", 0),
                                DropdownOption("淡出", 1),
                                DropdownOption("上滑", 2),
                                DropdownOption("下滑", 3),
                                DropdownOption("左滑", 4),
                                DropdownOption("右滑", 5)
                            )
                        )
                    }
                    item("退出动画时长") {
                        SliderInputField(
                            value = hitokotoExitAnimDuration,
                            onValueChange = {
                                hitokotoExitAnimDuration = it
                                AodSettings.hitokoto.exitAnimDuration = it
                            },
                            valueRange = 0f..2000f,
                            label = "退出动画时长 (ms)"
                        )
                    }
                    item("刷新间隔") {
                        SliderInputField(
                            value = hitokotoUpdateInterval.toFloat(),
                            onValueChange = {
                                hitokotoUpdateInterval = it.toInt().coerceAtLeast(AodSettings.MIN_UPDATE_INTERVAL)
                                AodSettings.hitokoto.updateInterval = hitokotoUpdateInterval
                            },
                            valueRange = 30f..600f,
                            label = "刷新间隔（秒，≥30）"
                        )
                    }
                }

                // 行为设置分组
                SettingsGroup(
                    label = "行为设置",
                    searchQuery = query,
                    defaultExpanded = false
                ) {
                    item("歌词播放时隐藏一言") {
                        SwitchField(
                            checked = hideHitokotoWhenLyric,
                            onCheckedChange = {
                                hideHitokotoWhenLyric = it
                                AodSettings.behavior.hideHitokotoWhenLyric = it
                            },
                            label = "隐藏一言",
                            description = "歌词正在播放时自动隐藏一言显示"
                        )
                    }
                    if (!hideHitokotoWhenLyric) {
                        item("显示顺序") {
                            val labelMap = mapOf(
                                "lyric" to "歌词",
                                "hitokoto" to "一言"
                            )
                            Column {
                                Text(
                                    text = "显示顺序",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = "长按拖动调整歌词与一言的上下层级",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                DragSortField(
                                    items = displayOrder,
                                    onReorder = { newList ->
                                        displayOrder = newList
                                        AodSettings.behavior.displayOrder = newList.toMutableList()
                                    },
                                    labelOf = { labelMap[it] ?: it }
                                ) { id ->
                                    Text(
                                        text = labelMap[id] ?: id,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
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
