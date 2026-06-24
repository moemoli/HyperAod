package moe.imoli.hyperaod.aod

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextSwitcher
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import moe.imoli.hyperaod.AodSettings
import moe.imoli.hyperaod.AodSettings.Alignment.*
import moe.imoli.hyperaod.ModuleMain
import moe.imoli.hyperaod.ui.anim.AnimCreator

/**
 * 歌词与一言共享的 TextSwitcher。
 *
 * 当 [AodSettings.BehaviorSettings.hideHitokotoWhenLyric] 为 true 时，
 * 歌词和一言共用同一个 [TextSwitcher]，根据当前显示模式动态切换动画：
 *
 * - 从一言切到歌词：一言退出动画 → 歌词进入动画
 * - 从歌词切到一言：歌词退出动画 → 一言进入动画
 * - 同模式内更新：使用该模式自身的进入/退出动画
 *
 * 由 [LyricModifier] 在 init 时创建，[HitokotoModifier] 通过 [instance] 获取。
 */
object SharedTextSwitcher {

    /** 当前显示模式 */
    enum class Mode { LYRIC, HITOKOTO }

    private var switcher: TextSwitcher? = null
    private var currentMode: Mode? = null

    /** 获取共享实例，未初始化时返回 null */
    val instance: TextSwitcher? get() = switcher

    /**
     * 初始化共享 TextSwitcher。
     *
     * 由 [LyricModifier.init] 调用，创建视图并添加到容器。
     * 使用歌词的样式作为默认 TextView 样式。
     *
     * @return 创建的 TextSwitcher，若已初始化则返回现有实例
     */
    fun init(context: Context, container: FrameLayout?): TextSwitcher {
        switcher?.let { return it }

        val ts = TextSwitcher(context)
        ts.setFactory {
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
                setTextColor(AodSettings.lyric.fontColor.toInt())
            }
        }

        // 默认歌词动画
        ts.inAnimation = AnimCreator.enter(AodSettings.lyric.enterAnim, AodSettings.lyric.enterAnimDuration)
        ts.outAnimation = AnimCreator.exit(AodSettings.lyric.exitAnim, AodSettings.lyric.exitAnimDuration)

        ts.layoutParams = FrameLayout.LayoutParams(
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

        container?.apply {
            val aodContent = container.asResolver()
                .firstField { name = "mAodContent" }
                .get() as ViewGroup
            aodContent.addView(ts)
            ts.visibility = android.view.View.VISIBLE
            ts.bringToFront()
            ts.requestLayout()
        }

        switcher = ts
        currentMode = null
        if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "SharedTextSwitcher initialized")
        return ts
    }

    /**
     * 切换到歌词模式，更新动画并显示文本。
     *
     * - 从一言切来：退出动画 = 一言退出，进入动画 = 歌词进入
     * - 歌词内部更新：退出动画 = 歌词退出，进入动画 = 歌词进入
     */
    fun showLyric(text: CharSequence) {
        val ts = switcher ?: return
        if (currentMode != Mode.LYRIC) {
            // 从一言切到歌词：一言退出 + 歌词进入
            ts.outAnimation = AnimCreator.exit(AodSettings.hitokoto.exitAnim, AodSettings.hitokoto.exitAnimDuration)
            ts.inAnimation = AnimCreator.enter(AodSettings.lyric.enterAnim, AodSettings.lyric.enterAnimDuration)
            currentMode = Mode.LYRIC
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "SharedTextSwitcher: HITOKOTO -> LYRIC")
        } else {
            // 歌词内部更新
            ts.outAnimation = AnimCreator.exit(AodSettings.lyric.exitAnim, AodSettings.lyric.exitAnimDuration)
            ts.inAnimation = AnimCreator.enter(AodSettings.lyric.enterAnim, AodSettings.lyric.enterAnimDuration)
        }
        ts.setText(text)
    }

    /**
     * 切换到一言模式，更新动画并显示文本。
     *
     * - 从歌词切来：退出动画 = 歌词退出，进入动画 = 一言进入
     * - 一言内部更新：退出动画 = 一言退出，进入动画 = 一言进入
     */
    fun showHitokoto(text: CharSequence) {
        val ts = switcher ?: return
        if (currentMode != Mode.HITOKOTO) {
            // 从歌词切到一言：歌词退出 + 一言进入
            ts.outAnimation = AnimCreator.exit(AodSettings.lyric.exitAnim, AodSettings.lyric.exitAnimDuration)
            ts.inAnimation = AnimCreator.enter(AodSettings.hitokoto.enterAnim, AodSettings.hitokoto.enterAnimDuration)
            currentMode = Mode.HITOKOTO
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "SharedTextSwitcher: LYRIC -> HITOKOTO")
        } else {
            // 一言内部更新
            ts.outAnimation = AnimCreator.exit(AodSettings.hitokoto.exitAnim, AodSettings.hitokoto.exitAnimDuration)
            ts.inAnimation = AnimCreator.enter(AodSettings.hitokoto.enterAnim, AodSettings.hitokoto.enterAnimDuration)
        }
        ts.setText(text)
    }

    /**
     * 释放共享 TextSwitcher，移除视图。
     */
    fun close() {
        switcher?.let { view ->
            (view.parent as? ViewGroup)?.removeView(view)
        }
        switcher = null
        currentMode = null
    }
}