package moe.imoli.hyperaod.ui.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import moe.imoli.hyperaod.ui.theme.HyperAodTheme
import kotlin.math.roundToInt

/**
 * HyperOS 3 风格的拖动排序组件
 *
 * 列表项通过长按拖动来重新排列顺序。
 * 左侧显示拖动手柄（6 点图案），右侧为自定义内容。
 *
 * 使用方式：
 * ```kotlin
 * DragSortField(
 *     items = myList,
 *     onReorder = { newList -> myList = newList },
 *     labelOf = { it.name }
 * ) { item ->
 *     Text(item.name)
 * }
 * ```
 *
 * @param items 待排序的列表
 * @param onReorder 排序完成回调，传入新的有序列表
 * @param labelOf 从 item 提取标签（用于搜索匹配）
 * @param modifier Modifier
 * @param content 每个列表项的内容 Composable
 */
@Composable
fun <T> DragSortField(
    items: List<T>,
    onReorder: (List<T>) -> Unit,
    labelOf: (T) -> String,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    var draggingIndex by remember { mutableIntStateOf(-1) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var itemHeight by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(vertical = 8.dp)
    ) {
        items.forEachIndexed { index, item ->
            val isDragging = draggingIndex == index
            val elevation by animateDpAsState(
                targetValue = if (isDragging) 8.dp else 0.dp,
                label = "dragElevation"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        if (isDragging) {
                            IntOffset(0, dragOffsetY.roundToInt())
                        } else {
                            IntOffset.Zero
                        }
                    }
                    .then(
                        if (isDragging) {
                            Modifier
                                .shadow(elevation, RoundedCornerShape(12.dp))
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                    RoundedCornerShape(12.dp)
                                )
                        } else {
                            Modifier
                        }
                    )
                    .onGloballyPositioned { coordinates ->
                        if (isDragging || itemHeight == 0f) {
                            itemHeight = coordinates.size.height.toFloat()
                        }
                    }
                    .pointerInput(index) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggingIndex = index
                                dragOffsetY = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetY += dragAmount.y

                                val targetIndex = when {
                                    dragOffsetY > itemHeight * 0.6f && draggingIndex < items.lastIndex -> draggingIndex + 1
                                    dragOffsetY < -itemHeight * 0.6f && draggingIndex > 0 -> draggingIndex - 1
                                    else -> -1
                                }
                                if (targetIndex >= 0) {
                                    val mutable = items.toMutableList()
                                    val moved = mutable.removeAt(draggingIndex)
                                    mutable.add(targetIndex, moved)
                                    onReorder(mutable)
                                    dragOffsetY -= itemHeight * if (targetIndex > draggingIndex) 1f else -1f
                                    draggingIndex = targetIndex
                                }
                            },
                            onDragEnd = {
                                draggingIndex = -1
                                dragOffsetY = 0f
                            },
                            onDragCancel = {
                                draggingIndex = -1
                                dragOffsetY = 0f
                            }
                        )
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 拖动手柄（纯视觉指示）
                DragHandle()

                Spacer(modifier = Modifier.width(12.dp))

                // 内容
                Box(modifier = Modifier.weight(1f)) {
                    content(item)
                }
            }
        }
    }
}

/**
 * 拖动手柄：6 点排列（2 列 3 行），标准拖拽指示图案。
 */
@Composable
private fun DragHandle(modifier: Modifier = Modifier) {
    val dotColor = MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(
        modifier = modifier
            .size(width = 16.dp, height = 24.dp)
    ) {
        val dotRadius = 2.dp.toPx()
        val colSpacing = 6.dp.toPx()
        val rowSpacing = 6.dp.toPx()
        val startX = (size.width - colSpacing) / 2f
        val startY = (size.height - rowSpacing * 2) / 2f

        for (row in 0..2) {
            for (col in 0..1) {
                drawCircle(
                    color = dotColor,
                    radius = dotRadius,
                    center = Offset(
                        x = startX + col * colSpacing,
                        y = startY + row * rowSpacing
                    )
                )
            }
        }
    }
}

// ========================
// Preview
// ========================

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun DragSortFieldPreview() {
    HyperAodTheme {
        var items by remember {
            mutableStateOf(listOf("歌词显示", "一言显示", "封面显示", "时钟显示", "天气显示"))
        }

        Column(modifier = Modifier.padding(16.dp)) {
            DragSortField(
                items = items,
                onReorder = { items = it },
                labelOf = { it }
            ) { item ->
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}