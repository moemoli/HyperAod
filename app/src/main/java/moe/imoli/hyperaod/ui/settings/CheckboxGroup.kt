package moe.imoli.hyperaod.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import moe.imoli.hyperaod.ui.theme.HyperAodTheme

/**
 * HyperOS 3 风格的复选框组（父组件）
 *
 * 左上角为标签，右上角为全选/全不选按钮；
 * 子组件通过 [content] 插槽传入。
 *
 * @param label 组标题
 * @param allChecked 是否全部选中
 * @param onSelectAll 全选/全不选回调，true = 全选，false = 全不选
 * @param modifier Modifier
 * @param content 子组件插槽，通常放置多个 [CheckboxItem]
 */
@Composable
fun CheckboxGroup(
    label: String,
    allChecked: Boolean,
    onSelectAll: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp)
    ) {
        // 顶部：标签 + 全选复选框
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectAll(!allChecked) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
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

        Spacer(modifier = Modifier.height(4.dp))

        // 子组件
        content()
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
