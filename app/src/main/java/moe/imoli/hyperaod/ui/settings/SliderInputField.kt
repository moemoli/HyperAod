package moe.imoli.hyperaod.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.imoli.hyperaod.ui.theme.HyperAodTheme
import java.util.Locale
import kotlin.math.roundToInt

/**
 * HyperOS 3 风格的滑块 + 输入框组合组件
 *
 * 左侧为 Slider，右侧为数字输入框，两者数值双向同步。
 * 输入框仅接受数字/浮点数，失焦或回车时提交，自动钳位到 [valueRange]。
 *
 * @param value 当前值
 * @param onValueChange 值变化回调
 * @param valueRange 取值范围
 * @param step 步长，0 表示连续（默认 0）
 * @param label 可选的标签文本，显示在滑块上方
 * @param modifier Modifier
 * @param decimals 小数位数，默认 0（整数模式）
 */
@Composable
fun SliderInputField(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    step: Int = 0,
    label: String? = null,
    modifier: Modifier = Modifier,
    decimals: Int = 0
) {
    // 输入框的文本状态
    var textFieldValue by remember { mutableStateOf(formatValue(value, decimals)) }
    // 记录输入框是否正在编辑
    var isEditing by remember { mutableStateOf(false) }

    // 当外部 value 变化（如拖动滑块）且输入框未在编辑时，同步文本
    if (!isEditing) {
        textFieldValue = formatValue(value, decimals)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // 标签行
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 左侧：Slider
            Slider(
                value = value,
                onValueChange = { newValue ->
                    val clamped = newValue.coerceIn(valueRange)
                    onValueChange(clamped)
                },
                valueRange = valueRange,
                steps = if (step > 0) {
                    ((valueRange.endInclusive - valueRange.start) / step).roundToInt() - 1
                } else 0,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            // 右侧：数字输入框
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newText ->
                    isEditing = true
                    // 过滤：只允许数字、小数点、负号
                    val filtered = filterNumericInput(newText, decimals)
                    textFieldValue = filtered
                    // 实时解析并同步（合法时）
                    parsedValue(filtered)?.let { parsed ->
                        val clamped = parsed.coerceIn(valueRange)
                        onValueChange(clamped)
                    }
                },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (decimals > 0) KeyboardType.Decimal else KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        commitAndSync(textFieldValue, valueRange, decimals, onValueChange)
                        isEditing = false
                    }
                ),
                modifier = Modifier
                    .width(72.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        innerTextField()
                    }
                }
            )
        }
    }
}

// ========================
// 内部工具函数
// ========================

/**
 * 格式化数值为显示字符串
 */
private fun formatValue(value: Float, decimals: Int): String {
    return if (decimals <= 0) {
        value.roundToInt().toString()
    } else {
        String.format(Locale.US, "%.${decimals}f", value)
    }
}

/**
 * 过滤输入文本，只保留合法的数字字符
 */
private fun filterNumericInput(text: String, decimals: Int): String {
    if (text.isEmpty()) return text

    val sb = StringBuilder()
    var hasDot = false
    var hasMinus = false

    for (ch in text) {
        when {
            ch == '-' && !hasMinus && sb.isEmpty() -> {
                hasMinus = true
                sb.append(ch)
            }
            ch == '.' && !hasDot && decimals > 0 -> {
                hasDot = true
                sb.append(ch)
            }
            ch.isDigit() -> sb.append(ch)
        }
    }
    return sb.toString()
}

/**
 * 尝试将文本解析为 Float
 */
private fun parsedValue(text: String): Float? {
    if (text.isEmpty() || text == "-" || text == ".") return null
    return text.toFloatOrNull()
}

/**
 * 提交输入框的值并同步（失焦/回车时）
 */
private fun commitAndSync(
    text: String,
    range: ClosedFloatingPointRange<Float>,
    decimals: Int,
    onValueChange: (Float) -> Unit
) {
    parsedValue(text)?.let { parsed ->
        onValueChange(parsed.coerceIn(range))
    }
}

// ========================
// Preview
// ========================

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SliderInputFieldPreview() {
    HyperAodTheme {
        var value1 by remember { mutableStateOf(50f) }
        var value2 by remember { mutableStateOf(0.5f) }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 整数滑块
            SliderInputField(
                value = value1,
                onValueChange = { value1 = it },
                valueRange = 0f..100f,
                label = "音量"
            )

            // 浮点数滑块
            SliderInputField(
                value = value2,
                onValueChange = { value2 = it },
                valueRange = 0f..1f,
                label = "透明度",
                decimals = 2
            )
        }
    }
}
