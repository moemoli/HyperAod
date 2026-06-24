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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
 * HyperOS 3 风格的可折叠复选框组
 *
 * 顶部为分组标题（点击展开/折叠）+ 全选复选框；
 * 子组件通过 [content] 插槽传入，默认折叠。
 *
 * @param label 组标题
 * @param allChecked 是否全部选中
 * @param onSelectAll 全选/全不选回调，true = 全选，false = 全不选
 * @param modifier Modifier
 * @param defaultExpanded 默认展开状态
 * @param content 子组件插槽，通常放置多个 [CheckboxItem]
 */
@Composable
fun CheckboxGroup(
    label: String,
    allChecked: Boolean,
    onSelectAll: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    defaultExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(defaultExpanded) }

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
        // 顶部：折叠箭头 + 标签 + 全选复选框
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 折叠箭头
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .rotate(arrowRotation)
                    .clickable { expanded = !expanded },
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

            // 标签（点击也可折叠）
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .clickable { expanded = !expanded }
                    .padding(start = 8.dp)
            )

            // 全选复选框
            Checkbox(
                checked = allChecked,
                onCheckedChange = onSelectAll,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }

        // 子组件（可折叠）
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(tween(200)),
            exit = shrinkVertically(tween(200))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * 复选框子组件
 *
 * 左侧为标签，右侧为复选框。点击整行可切换选中状态。
 *
 * @param checked 是否选中
 * @param onCheckedChange 选中状态变化回调
 * @param label 标签文本
 * @param modifier Modifier
 */
@Composable
fun CheckboxItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(start = 8.dp, top = 6.dp, end = 0.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

// ========================
// Preview
// ========================

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CheckboxGroupPreview() {
    HyperAodTheme {
        var items by remember {
            mutableStateOf(
                mapOf(
                    "歌词显示" to true,
                    "封面显示" to true,
                    "进度条" to false,
                    "动画效果" to false
                )
            )
        }

        val allChecked = items.values.all { it }

        Column(modifier = Modifier.padding(16.dp)) {
            CheckboxGroup(
                label = "息屏元素",
                allChecked = allChecked,
                onSelectAll = { select ->
                    items = items.mapValues { select }
                }
            ) {
                items.forEach { (name, checked) ->
                    CheckboxItem(
                        checked = checked,
                        onCheckedChange = { newValue ->
                            items = items.toMutableMap().apply { put(name, newValue) }
                        },
                        label = name
                    )
                }
            }
        }
    }
}