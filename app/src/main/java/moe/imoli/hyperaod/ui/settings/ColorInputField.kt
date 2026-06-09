package moe.imoli.hyperaod.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.imoli.hyperaod.ui.theme.HyperAodTheme

/**
 * HyperOS 3 风格的颜色输入组件
 *
 * 左侧为 16 进制 RGBA 颜色代码输入框（格式：0x12345678），
 * 右侧小矩形框实时展示输入的颜色。
 *
 * @param value 当前颜色值
 * @param onValueChange 颜色变化回调
 * @param label 可选的标签文本，显示在颜色输入区域上方
 * @param modifier Modifier
 */
@Composable
fun ColorInputField(
    value: Color,
    onValueChange: (Color) -> Unit,
    label: String? = null,
    modifier: Modifier = Modifier
) {
    // 将 Color 转为 0xRRGGBBAA 格式文本
    fun colorToHex(color: Color): String {
        val argb = color.toArgb()
        // Android ARGB → 取 RRGGBBAA 排列
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = argb and 0xFF
        val a = (argb shr 24) and 0xFF
        return "0x%02X%02X%02X%02X".format(r, g, b, a)
    }

    // 解析 0xRRGGBBAA 文本为 Color
    fun hexToColor(text: String): Color? {
        val hex = text.removePrefix("0x").removePrefix("0X")
        if (hex.length != 8) return null
        return try {
            val r = hex.substring(0, 2).toInt(16)
            val g = hex.substring(2, 4).toInt(16)
            val b = hex.substring(4, 6).toInt(16)
            val a = hex.substring(6, 8).toInt(16)
            Color(r, g, b, a)
        } catch (_: NumberFormatException) {
            null
        }
    }

    // 过滤输入，只保留 0x 前缀和合法 hex 字符
    fun filterHexInput(text: String): String {
        if (text.isEmpty()) return text
        val sb = StringBuilder()
        var i = 0
        // 保留 0x 或 0X 前缀
        if (text.startsWith("0x") || text.startsWith("0X")) {
            sb.append(text.substring(0, 2))
            i = 2
        }
        // 只保留 hex 字符，最多 8 位
        var count = 0
        while (i < text.length && count < 8) {
            val ch = text[i]
            if (ch in '0'..'9' || ch in 'a'..'f' || ch in 'A'..'F') {
                sb.append(ch.uppercaseChar())
                count++
            }
            i++
        }
        return sb.toString()
    }

    // 输入框文本状态
    var textFieldValue by remember { mutableStateOf(colorToHex(value)) }
    var isEditing by remember { mutableStateOf(false) }

    // 外部 value 变化且未编辑时同步
    if (!isEditing) {
        textFieldValue = colorToHex(value)
    }

    val lineColor = MaterialTheme.colorScheme.outline
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 左侧：hex 输入框
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newText ->
                    isEditing = true
                    val filtered = filterHexInput(newText)
                    textFieldValue = filtered
                    // 实时解析并同步
                    hexToColor(filtered)?.let { onValueChange(it) }
                },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        hexToColor(textFieldValue)?.let { onValueChange(it) }
                        isEditing = false
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = if (isEditing) {
                            Modifier.drawBehind {
                                drawLine(
                                    color = lineColor,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                        } else {
                            Modifier
                        },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        innerTextField()
                    }
                }
            )

            // 右侧：颜色预览矩形
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(value)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
    }
}

// ========================
// Preview
// ========================

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ColorInputFieldPreview() {
    HyperAodTheme {
        var color1 by remember { mutableStateOf(Color(0xFF1565C0)) }
        var color2 by remember { mutableStateOf(Color(0x80FF6D00)) }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ColorInputField(
                value = color1,
                onValueChange = { color1 = it },
                label = "主色调"
            )

            ColorInputField(
                value = color2,
                onValueChange = { color2 = it },
                label = "半透明橙色"
            )
        }
    }
}
