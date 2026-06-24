package moe.imoli.hyperaod.aod

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextSwitcher
import android.widget.TextView
import com.hchen.superlyricapi.ISuperLyricReceiver
import com.hchen.superlyricapi.SuperLyricData
import com.hchen.superlyricapi.SuperLyricHelper
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import moe.imoli.hyperaod.AodSettings
import moe.imoli.hyperaod.AodSettings.Alignment.*
import moe.imoli.hyperaod.ModuleMain
import moe.imoli.hyperaod.ui.anim.AnimCreator

/**
 * 歌词 Modifier
 *
 * 在 AOD 上显示当前播放的歌词，通过 SuperLyric API 接收歌词数据，
 * 使用 [TextSwitcher] 实现歌词切换动画。
 */
object LyricModifier : AodModifier() {

    private var receiver: ISuperLyricReceiver.Stub? = null
    private var lyricSwitcher: LyricSwitcher? = null
    private var initialized = false

    override fun init(
        aodView: FrameLayout?,
        container: FrameLayout?,
        context: Context,
        dozeHost: Any
    ) {
        if (!AodSettings.lyric.enable) return

        // 防止重复初始化：先清理旧的
        if (initialized) {
            close()
        }

        // 歌词样式初始化
        val switcher = LyricSwitcher()
        switcher.init(context)
        lyricSwitcher = switcher

        container?.apply {
            val aodContent = container.asResolver()
                .firstField { name = "mAodContent" }
                .get() as ViewGroup
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "add lyricSwitcher")
            aodContent.addView(switcher.lyric)
            // 确保视图可见并触发布局
            switcher.lyric.visibility = android.view.View.VISIBLE
            switcher.lyric.bringToFront()
            switcher.lyric.requestLayout()
        }

        // 歌词接收
        receiver = object : ISuperLyricReceiver.Stub() {
            override fun onLyric(publisher: String?, data: SuperLyricData?) {
                if (data?.hasLyric() ?: return) {
                    val text = data.lyric?.text ?: return
                    container?.post {
                        if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "receive lyric: ")
                        AodSettings.lyricPlaying = true
                        HitokotoModifier.updateVisibility()
                        refresh(container, dozeHost)
                        switcher.update(text)
                    }
                }
            }

            override fun onStop(publisher: String?, data: SuperLyricData?) {
                container?.post {
                    AodSettings.lyricPlaying = false
                    HitokotoModifier.updateVisibility()
                    refresh(container, dozeHost)
                    switcher.update("")
                }
            }
        }.apply {
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "register receiver")
            SuperLyricHelper.registerReceiver(this)
        }
        initialized = true
    }

    override fun update() {
    }

    override fun close() {
        receiver?.let {
            if (SuperLyricHelper.isReceiverRegistered(it)) {
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "unregister receiver")
                SuperLyricHelper.unregisterReceiver(it)
            }
        }
        receiver = null
        // 移除旧视图
        lyricSwitcher?.lyric?.let { view ->
            (view.parent as? ViewGroup)?.removeView(view)
        }
        lyricSwitcher = null
        initialized = false
    }

    /**
     * 歌词切换器
     *
     * 封装 [TextSwitcher] 的创建和更新逻辑，根据 [AodSettings.lyric] 配置样式。
     */
    class LyricSwitcher {
        lateinit var lyric: TextSwitcher

        /**
         * 初始化 TextSwitcher，根据当前设置配置样式和动画。
         */
        fun init(context: Context) {
            lyric = TextSwitcher(context)
            // 强制软件渲染，避免硬件层合成问题
            lyric.setFactory {
                TextView(context).apply {
                    // 对齐方式
                    gravity = when (AodSettings.lyric.alignment) {
                        Center -> Gravity.CENTER
                        Left -> Gravity.START
                        Right -> Gravity.END
                    }

                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )

                    // 字体大小
                    textSize = AodSettings.lyric.fontSize

                    // 字体颜色
                    val color = AodSettings.lyric.fontColor.toInt()
                    setTextColor(color)
                    if (ModuleMain.DEBUG) Log.d(
                        ModuleMain.TAG,
                        "TextView created: color=0x, size="
                    )
                }
            }
            // 切换动画
            lyric.inAnimation =
                AnimCreator.enter(AodSettings.lyric.enterAnim, AodSettings.lyric.enterAnimDuration)
            lyric.outAnimation =
                AnimCreator.exit(AodSettings.lyric.exitAnim, AodSettings.lyric.exitAnimDuration)

            // 布局参数（含外边距）
            lyric.layoutParams =
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(
                        AodSettings.lyric.marginLeft.toInt(),
                        AodSettings.lyric.marginTop.toInt(),
                        AodSettings.lyric.marginRight.toInt(),
                        AodSettings.lyric.marginBottom.toInt()
                    )
                }
        }

        /**
         * 更新歌词文本。
         *
         * @param text 要显示的歌词文本，空字符串表示清除
         */
        fun update(text: CharSequence) {
            lyric.setText(text)
            lyric.invalidate()
            lyric.requestLayout()
            if (ModuleMain.DEBUG) {
                val tv = lyric.currentView as? TextView
                Log.d(
                    ModuleMain.TAG, "update: text=\"\", tv.text=\"\", " +
                            "w=, h=, vis=, " +
                            "alpha=, parent=, " +
                            "childCount="
                )
            }
        }
    }
}