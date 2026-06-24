package moe.imoli.hyperaod.ui.settings

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import moe.imoli.hyperaod.R
import moe.imoli.hyperaod.ui.theme.HyperAodTheme

/**
 * HyperOS 3 风格的可点击组件
 *
 * 类似 DropdownField，但无动画与下拉菜单。
 * 最左边为可选图标，点击执行自定义操作。
 *
 * @param label 标签文本
 * @param onClick 点击回调
 * @param description 可选的描述文本，显示在标签下方
 * @param icon 可选的图标 Painter
 * @param modifier Modifier
 */
@Composable
fun ClickableField(
    label: String,
    onClick: () -> Unit,
    description: String? = null,
    icon: Painter? = null,
    modifier: Modifier = Modifier
) {
    val lineColor = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 可选图标
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        // 标签 + 描述
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (description != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 右侧箭头（静态，无动画）
        Box(
            modifier = Modifier.size(20.dp),
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
}

// ========================
// Preview
// ========================

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ClickableFieldPreview() {
    HyperAodTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ClickableField(
                label = "重启系统界面",
                description = "重启 SystemUI 以应用更改",
                icon = androidx.compose.ui.res.painterResource(id = R.drawable.ic_restart),
                onClick = {}
            )

            ClickableField(
                label = "关于",
                icon = androidx.compose.ui.res.painterResource(id = R.drawable.ic_about),
                onClick = {}
            )

            ClickableField(
                label = "无图标的点击项",
                onClick = {}
            )
        }
    }
}
