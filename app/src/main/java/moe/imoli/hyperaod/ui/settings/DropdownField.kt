package moe.imoli.hyperaod.ui.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import moe.imoli.hyperaod.ui.theme.HyperAodTheme

/**
 * 下拉选项，将显示文本与实际值分离
 *
 * @param label 显示在菜单和选择框中的文本
 * @param value 实际存储/返回的值
 */
data class DropdownOption<T>(
    val label: String,
    val value: T
)

/**
 * HyperOS 3 风格的下拉选择组件
 *
 * 左侧展示当前选中内容，右侧为箭头图标。
 * 点击展开菜单，箭头动画旋转向下；选择后箭头旋转回向右。
 *
 * @param value 当前选中的值
 * @param onValueChange 选中值变化回调
 * @param options 可选项列表
 * @param labelMapper 将选项映射为显示文本
 * @param label 可选的标签文本，显示在下拉框上方
 * @param modifier Modifier
 */
@Composable
fun <T> DropdownField(
    value: T,
    onValueChange: (T) -> Unit,
    options: List<T>,
    labelMapper: (T) -> String = { it.toString() },
    label: String? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    // 记录触发区域宽度，用于让菜单等宽
    var triggerWidth by remember { mutableIntStateOf(0) }

    // 箭头旋转动画：收起 0° → 展开 90°（朝下）
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "arrowRotation"
    )

    val density = LocalDensity.current

    Column(modifier = modifier.fillMaxWidth()) {
        // 标签
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 下拉触发区域 + 菜单放在一个 Box 中
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .clickable { expanded = !expanded }
                    .onGloballyPositioned { coordinates ->
                        triggerWidth = coordinates.size.width
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 左侧：选中内容
                Text(
                    text = labelMapper(value),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // 右侧：旋转箭头（Canvas 手绘）
                val arrowColor = MaterialTheme.colorScheme.onSurfaceVariant
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotation),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(16.dp)) {
                        val strokeW = 2.5.dp.toPx()
                        val cap = StrokeCap.Round
                        // ">" 形箭头，100° 开口（cos50°≈0.6428, sin50°≈0.7660）
                        val cx = size.width * 0.68f
                        val cy = size.height * 0.5f
                        val r = size.width * 0.38f
                        val dx = r * 0.6428f
                        val dy = r * 0.7660f
                        drawLine(
                            color = arrowColor,
                            start = Offset(cx - dx, cy - dy),
                            end = Offset(cx, cy),
                            strokeWidth = strokeW,
                            cap = cap
                        )
                        drawLine(
                            color = arrowColor,
                            start = Offset(cx, cy),
                            end = Offset(cx - dx, cy + dy),
                            strokeWidth = strokeW,
                            cap = cap
                        )
                    }
                }
            }

            // 下拉菜单：宽度与触发区域一致
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(0.dp, 6.dp),
                modifier = Modifier
                    .width(with(density) { triggerWidth.toDp() })
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                options.forEach { option ->
                    val isSelected = option == value
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = labelMapper(option),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    )
                }
            }
        }
    }
}

/**
 * 便捷重载：使用 [DropdownOption] 列表，显示文本与实际值分离
 *
 * 用法：
 * ```
 * var anim by remember { mutableStateOf(1) }
 * DropdownField(
 *     value = anim,
 *     onValueChange = { anim = it },
 *     options = listOf(
 *         DropdownOption("无", 0),
 *         DropdownOption("淡入", 1),
 *         DropdownOption("上滑", 2)
 *     ),
 *     label = "进入动画"
 * )
 * ```
 */
@Composable
fun <T> DropdownField(
    value: T,
    onValueChange: (T) -> Unit,
    label: String? = null,
    options: List<DropdownOption<T>>,
    modifier: Modifier = Modifier
) {
    DropdownField(
        value = value,
        onValueChange = onValueChange,
        options = options.map { it.value },
        labelMapper = { v -> options.first { it.value == v }.label },
        label = label,
        modifier = modifier
    )
}

// ========================
// Preview
// ========================

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun DropdownFieldPreview() {
    HyperAodTheme {
        var selectedTheme by remember { mutableStateOf(1) }
        var selectedAnim by remember { mutableStateOf(1) }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            DropdownField(
                value = selectedTheme,
                onValueChange = { selectedTheme = it },
                "主题模式",
                listOf(
                    DropdownOption("浅色", 0),
                    DropdownOption("深色", 1),
                    DropdownOption("跟随系统", 2)
                ),

            )

            DropdownField(
                value = selectedAnim,
                onValueChange = { selectedAnim = it },
                "进入动画",
                options = listOf(
                    DropdownOption("无", 0),
                    DropdownOption("淡入", 1),
                    DropdownOption("上滑", 2)
                ),

            )
        }
    }
}
