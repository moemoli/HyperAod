package moe.imoli.hyperaod.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import moe.imoli.hyperaod.ui.theme.HyperAodTheme

/**
 * SettingsGroup 的构建作用域，提供 [item] 方法注册子组件
 */
class SettingsGroupScope {
    private val _items = mutableStateListOf<SearchableItem>()

    val items: List<SearchableItem> get() = _items

    /**
     * 注册一个可搜索的子组件
     *
     * @param label 搜索匹配标签
     * @param content 子组件内容
     */
    fun item(label: String, content: @Composable () -> Unit) {
        _items.add(SearchableItem(label, content))
    }
}

/**
 * 可折叠的设置分组组件
 *
 * 顶部为分组标题（点击展开/折叠），下方为子组件列表。
 * 支持搜索筛选：当 [searchQuery] 不为空时，只显示匹配的子组件，
 * 且分组标题始终可见，不会因无匹配项而隐藏。
 *
 * 使用 DSL 方式注册子组件：
 * ```
 * SettingsGroup(label = "显示设置", searchQuery = query) {
 *     item("歌词显示") { SwitchField(...) }
 *     item("封面显示") { SwitchField(...) }
 * }
 * ```
 *
 * @param label 分组标题
 * @param searchQuery 搜索关键词（由 [SearchableGroup] 传入），空表示不筛选
 * @param modifier Modifier
 * @param defaultExpanded 未搜索时的默认展开状态
 * @param builder 子组件构建器
 */
@Composable
fun SettingsGroup(
    label: String,
    searchQuery: String = "",
    modifier: Modifier = Modifier,
    defaultExpanded: Boolean = true,
    builder: SettingsGroupScope.() -> Unit
) {
    val scope = remember { SettingsGroupScope() }
    // 执行 builder 注册子组件
    remember(builder) { scope.apply(builder) }

    val items = scope.items

    var manuallyExpanded by remember { mutableStateOf(defaultExpanded) }

    val isSearching = searchQuery.isNotBlank()
    val expanded = if (isSearching) true else manuallyExpanded

    val filteredItems = remember(searchQuery, items) {
        if (searchQuery.isBlank()) items
        else items.filter { it.label.contains(searchQuery, ignoreCase = true) }
    }

    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "arrowRotation"
    )

    val lineColor = MaterialTheme.colorScheme.outline

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp)
    ) {
        // 分组标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (!isSearching) {
                        manuallyExpanded = !manuallyExpanded
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .rotate(arrowRotation),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(16.dp)) {
                    val strokeW = 2.5.dp.toPx()
                    val cap = StrokeCap.Round
                    val cx = size.width * 0.68f
                    val cy = size.height * 0.5f
                    val r = size.width * 0.38f
                    val dx = r * 0.6428f
                    val dy = r * 0.7660f
                    drawLine(
                        color = lineColor,
                        start = Offset(cx - dx, cy - dy),
                        end = Offset(cx, cy),
                        strokeWidth = strokeW,
                        cap = cap
                    )
                    drawLine(
                        color = lineColor,
                        start = Offset(cx, cy),
                        end = Offset(cx - dx, cy + dy),
                        strokeWidth = strokeW,
                        cap = cap
                    )
                }
            }
        }

        // 子组件
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(tween(200)),
            exit = shrinkVertically(tween(200))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isSearching && filteredItems.isEmpty()) {
                    Text(
                        text = "无匹配项",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                filteredItems.forEach { item ->
                    key(item.label) {
                        item.content()
                    }
                }
            }
        }
    }
}

// ========================
// Preview
// ========================

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SettingsGroupPreview() {
    HyperAodTheme {
        var switch1 by remember { mutableStateOf(true) }
        var switch2 by remember { mutableStateOf(false) }

        Column(modifier = Modifier.padding(16.dp)) {
            SettingsGroup(label = "显示设置") {
                item("歌词显示") {
                    SwitchField(
                        checked = switch1,
                        onCheckedChange = { switch1 = it },
                        label = "歌词显示",
                        description = "在息屏上显示当前播放歌词"
                    )
                }
                item("封面显示") {
                    SwitchField(
                        checked = switch2,
                        onCheckedChange = { switch2 = it },
                        label = "封面显示",
                        description = "在息屏上显示专辑封面"
                    )
                }
            }
        }
    }
}
