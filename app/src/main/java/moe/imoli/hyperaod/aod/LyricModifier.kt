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
 *
 * 当 [AodSettings.BehaviorSettings.hideHitokotoWhenLyric] 为 true 时，
 * 与 [HitokotoModifier] 共享 [SharedTextSwitcher]，切换时使用对应来源的动画。
 */
object LyricModifier : AodModifier() {

    private var receiver: ISuperLyricReceiver.Stub? = null
    private var lyricSwitcher: LyricSwitcher? = null
    private var initialized = false

    /** 是否使用共享 TextSwitcher */
    private val useShared get() = AodSettings.behavior.hideHitokotoWhenLyric

    override fun init(
        aodView: FrameLayout?,
        container: FrameLayout?,
        context: Context,
        dozeHost: Any
    ) {
        if (!AodSettings.lyric.enable) return

        if (initialized) {
            close()
        }

        if (useShared) {
            // 共享模式：由 LyricModifier 创建 SharedTextSwitcher
            SharedTextSwitcher.init(context, container)
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "lyric using SharedTextSwitcher")
        } else {
            // 独立模式：创建自己的 LyricSwitcher
            val switcher = LyricSwitcher()
            switcher.init(context)
            lyricSwitcher = switcher

            container?.apply {
                val aodContent = container.asResolver()
                    .firstField { name = "mAodContent" }
                    .get() as ViewGroup
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "add lyricSwitcher")
                aodContent.addView(switcher.lyric)
                switcher.lyric.visibility = android.view.View.VISIBLE
                switcher.lyric.bringToFront()
                switcher.lyric.requestLayout()
            }
        }

        // 歌词接收
        receiver = object : ISuperLyricReceiver.Stub() {
            override fun onLyric(publisher: String?, data: SuperLyricData?) {
                if (data?.hasLyric() ?: return) {
                    val text = data.lyric?.text ?: return
                    container?.post {
                        if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "receive lyric: $text")
                        AodSettings.lyricPlaying = true
                        HitokotoModifier.updateVisibility()
                        refresh(container, dozeHost)
                        if (useShared) {
                            SharedTextSwitcher.showLyric(text)
                        } else {
                            lyricSwitcher?.update(text)
                        }
                    }
                }
            }

            override fun onStop(publisher: String?, data: SuperLyricData?) {
                container?.post {
                    AodSettings.lyricPlaying = false
                    HitokotoModifier.updateVisibility()
                    refresh(container, dozeHost)
                    if (useShared) {
                        HitokotoModifier.restoreInShared()
                    } else {
                        lyricSwitcher?.update("")
                    }
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

        if (useShared) {
            SharedTextSwitcher.close()
        } else {
            lyricSwitcher?.lyric?.let { view ->
                (view.parent as? ViewGroup)?.removeView(view)
            }
        }
        lyricSwitcher = null
        initialized = false
    }

    /**
     * 歌词切换器（独立模式使用）
     */
    class LyricSwitcher {
        lateinit var lyric: TextSwitcher

        fun init(context: Context) {
            lyric = TextSwitcher(context)
            lyric.setFactory {
                TextView(context).apply {
                    gravity = when (AodSettings.lyric.alignment) {
                        Center -> Gravity.CENTER
                        Left -> Gravity.START
                        Right -> Gravity.END
                    }
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                    textSize = AodSettings.lyric.fontSize
                    val color = AodSettings.lyric.fontColor.toInt()
                    setTextColor(color)
                    if (ModuleMain.DEBUG) Log.d(
                        ModuleMain.TAG,
                        "TextView created: color=0x${color.toHexString()}, size=${textSize}"
                    )
                }
            }
            lyric.inAnimation =
                AnimCreator.enter(AodSettings.lyric.enterAnim, AodSettings.lyric.enterAnimDuration)
            lyric.outAnimation =
                AnimCreator.exit(AodSettings.lyric.exitAnim, AodSettings.lyric.exitAnimDuration)
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

        fun update(text: CharSequence) {
            lyric.setText(text)
            lyric.invalidate()
            lyric.requestLayout()
            if (ModuleMain.DEBUG) {
                val tv = lyric.currentView as? TextView
                Log.d(
                    ModuleMain.TAG, "lyric update: text=\"$text\", tv.text=\"${tv?.text}\", " +
                            "w=${lyric.width}, h=${lyric.height}, vis=${lyric.visibility}"
                )
            }
        }
    }
}