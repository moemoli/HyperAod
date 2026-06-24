package moe.imoli.hyperaod.ui.anim

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import moe.imoli.hyperaod.AodSettings

/**
 * 动画创建器
 *
 * 根据 [AodSettings.Anim] 配置创建对应的进入/退出动画。
 */
object AnimCreator {

    /**
     * 创建进入动画
     *
     * @param type 动画类型
     * @param duration 动画时长（毫秒）
     */
    fun enter(type: AodSettings.Anim.Enter, duration: Float): Animation = when (type) {
        AodSettings.Anim.Enter.None -> TranslateAnimation(0f, 0f, 0f, 0f)
        AodSettings.Anim.Enter.FadeIn -> AlphaAnimation(0f, 1f).apply { fillBefore = true }
        AodSettings.Anim.Enter.Up -> TranslateAnimation(0f, 0f, 500f, 0f)
        AodSettings.Anim.Enter.Down -> TranslateAnimation(0f, 0f, -500f, 0f)
        AodSettings.Anim.Enter.Left -> AnimationSet(false).apply {
            addAnimation(TranslateAnimation(0f, -1000f, 0f, 0f))
            addAnimation(AlphaAnimation(0f, 1f).apply { fillBefore = true })
        }
        AodSettings.Anim.Enter.Right -> AnimationSet(false).apply {
            addAnimation(TranslateAnimation(0f, 1000f, 0f, 0f))
            addAnimation(AlphaAnimation(0f, 1f).apply { fillBefore = true })
        }
    }.apply {
        this.duration = duration.toLong()
    }

    /**
     * 创建退出动画
     *
     * @param type 动画类型
     * @param duration 动画时长（毫秒）
     */
    fun exit(type: AodSettings.Anim.Exit, duration: Float): Animation = when (type) {
        AodSettings.Anim.Exit.None -> TranslateAnimation(0f, 0f, 0f, 0f)
        AodSettings.Anim.Exit.FadeOut -> AlphaAnimation(1f, 0f).apply { fillAfter = true }
        AodSettings.Anim.Exit.Up -> TranslateAnimation(0f, 0f, 0f, -500f)
        AodSettings.Anim.Exit.Down -> TranslateAnimation(0f, 0f, 0f, 500f)
        AodSettings.Anim.Exit.Left -> AnimationSet(false).apply {
            addAnimation(TranslateAnimation(0f, -1000f, 0f, 0f))
            addAnimation(AlphaAnimation(1f, 0f).apply { fillAfter = true })
        }
        AodSettings.Anim.Exit.Right -> AnimationSet(false).apply {
            addAnimation(TranslateAnimation(0f, 1000f, 0f, 0f))
            addAnimation(AlphaAnimation(1f, 0f).apply { fillAfter = true })
        }
    }.apply {
        this.duration = duration.toLong()
    }
}