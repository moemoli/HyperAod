package moe.imoli.hyperaod.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
 * HyperOS 3 风格的开关组件
 *
 * 左侧为标签文字，右侧为 Switch 开关。
 *
 * @param checked 当前开关状态
 * @param onCheckedChange 状态变化回调
 * @param label 标签文本
 * @param description 可选的描述文本，显示在标签下方
 * @param modifier Modifier
 */
@Composable
fun SwitchField(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    description: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 左侧：标签 + 描述
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
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

        // 右侧：Switch
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

// ========================
// Preview
// ========================

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SwitchFieldPreview() {
    HyperAodTheme {
        var enabled1 by remember { mutableStateOf(true) }
        var enabled2 by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SwitchField(
                checked = enabled1,
                onCheckedChange = { enabled1 = it },
                label = "歌词注入",
                description = "在息屏显示上显示当前播放歌词"
            )

            SwitchField(
                checked = enabled2,
                onCheckedChange = { enabled2 = it },
                label = "自动更新"
            )
        }
    }
}
