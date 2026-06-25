package moe.imoli.hyperaod.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.imoli.hyperaod.BuildConfig
import moe.imoli.hyperaod.R
import moe.imoli.hyperaod.ui.theme.HyperAodTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 关于界面
 *
 * 居中显示：应用图标、应用名称、版本号、版权信息。
 * 下方为社交图标行，点击执行自定义跳转。
 *
 * @param onBack 返回回调
 * @param modifier Modifier
 */
@Composable
fun AboutScreen(
    onBack: () -> Unit = {},
    showBackButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val iconTint = MaterialTheme.colorScheme.outlineVariant
    val iconBg = MaterialTheme.colorScheme.surfaceContainerLow

    fun openUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部返回按钮
        if (showBackButton) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    // 向左箭头（Canvas 手绘，与 DropdownField 镜像）
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val strokeW = 2.5.dp.toPx()
                        val cap = StrokeCap.Round
                        val cx = size.width * 0.32f
                        val cy = size.height * 0.5f
                        val r = size.width * 0.38f
                        val dx = r * 0.6428f
                        val dy = r * 0.7660f
                        drawLine(
                            color = iconTint,
                            start = Offset(cx + dx, cy - dy),
                            end = Offset(cx, cy),
                            strokeWidth = strokeW,
                            cap = cap
                        )
                        drawLine(
                            color = iconTint,
                            start = Offset(cx, cy),
                            end = Offset(cx + dx, cy + dy),
                            strokeWidth = strokeW,
                            cap = cap
                        )
                    }
                }
            }
        }

        // 居中内容
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            Box(
                modifier = Modifier
                    .background(
                        Color(0xffaac2da),
                        RoundedCornerShape(30.dp)
                    )
            ) {
                // 应用图标
                Image(
                    painter = painterResource(id = R.drawable.ic_sun_moon),
                    contentDescription = "应用图标",
                    modifier = Modifier
                        .size(100.dp)

                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 应用名称
            Text(
                text = "HyperAod",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 版本号
            Text(
                text = "v${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 版权信息
            Text(
                text = "© ${
                    DateTimeFormatter.ofPattern("yyyy").format(LocalDate.now())
                } moemoli\nAll Rights Reserved",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 社交图标行
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // GitHub
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg)
                        .clickable {
                            openUrl("https://github.com/moemoli/HyperAod")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_github),
                        contentDescription = "GitHub",
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(iconTint)
                    )
                }

                // QQ
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg)
                        .clickable {
                            openUrl("https://qm.qq.com/q/xPz96SDFSw")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_qq),
                        contentDescription = "QQ 群",
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(iconTint)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun AboutScreenPreview() {
    HyperAodTheme {
        AboutScreen()
    }
}
