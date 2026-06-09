package moe.imoli.hyperaod.ui.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.imoli.hyperaod.R
import moe.imoli.hyperaod.ui.theme.HyperAodTheme

/**
 * 可搜索的组件项
 *
 * @param label 用于搜索匹配的标签（不显示，仅用于筛选）
 * @param content 要展示的组件内容
 */
data class SearchableItem(
    val label: String,
    val content: @Composable () -> Unit
)

/**
 * HyperOS 3 风格的搜索筛选组件
 *
 * 顶部搜索框，下方通过 [content] 插槽放置内容。
 * 搜索关键词通过 lambda 传递给子组件，由子组件自行决定筛选逻辑。
 *
 * 支持两种用法：
 * 1. 直接放置 [SearchableItem] 列表（通过便捷重载）
 * 2. 放置 [SettingsGroup] 等自定义容器（通过 [content] 插槽）
 *
 * @param modifier Modifier
 * @param hint 搜索框占位提示文本
 * @param content 内容插槽，接收当前搜索关键词
 */
@Composable
fun SearchableGroup(
    modifier: Modifier = Modifier,
    hint: String = "搜索…",
    content: @Composable (query: String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val expanded = query.isNotEmpty() || isFocused

    // 动画参数
    val iconSize = 18.dp
    val iconAlpha by animateFloatAsState(
        targetValue = if (expanded) 0f else 1f,
        animationSpec = tween(durationMillis = 250),
        label = "iconAlpha"
    )
    val inputStartPadding by animateDpAsState(
        targetValue = if (expanded) 0.dp else iconSize + 4.dp,
        animationSpec = tween(durationMillis = 250),
        label = "inputStartPadding"
    )

    val lineColor = MaterialTheme.colorScheme.outline

    Column(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 搜索框容器
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 输入行
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            focusManager.clearFocus()
                        }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = inputStartPadding)
                            .focusRequester(focusRequester)
                            .onFocusChanged { state ->
                                isFocused = state.isFocused
                            },
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (query.isEmpty()) {
                                    Text(
                                        text = hint,
                                        style = TextStyle(
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "搜索",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(iconSize)
                            .alpha(iconAlpha)
                    )
                }

                // 下划线
                Spacer(modifier = Modifier.height(6.dp))
                if (expanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                drawLine(
                                    color = lineColor,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                    )
                }
            }
        }

        // 内容区域（搜索关键词传递给子组件）
        content(query.trim())
    }
}

/**
 * 便捷重载：直接传入 [SearchableItem] 列表
 */
@Composable
fun SearchableGroup(
    items: List<SearchableItem>,
    modifier: Modifier = Modifier,
    hint: String = "搜索…"
) {
    SearchableGroup(modifier = modifier, hint = hint) { query ->
        val filtered = remember(query, items) {
            if (query.isBlank()) items
            else items.filter { it.label.contains(query, ignoreCase = true) }
        }
        filtered.forEach { item ->
            item.content()
        }
    }
}

// ========================
// Preview
// ========================

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SearchableGroupPreview() {
    HyperAodTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            SearchableGroup { query ->
                SettingsGroup(
                    label = "显示设置",
                    searchQuery = query
                ) {
                    item("歌词显示") {
                        SwitchField(
                            checked = true,
                            onCheckedChange = {},
                            label = "歌词显示",
                            description = "在息屏上显示当前播放歌词"
                        )
                    }
                    item("封面显示") {
                        SwitchField(
                            checked = false,
                            onCheckedChange = {},
                            label = "封面显示",
                            description = "在息屏上显示专辑封面"
                        )
                    }
                }
            }
        }
    }
}
