package moe.imoli.hyperaod.ui.anim

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import moe.imoli.hyperaod.AodSettings

object AnimCreator {
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

    /**
     * 将多个 [Animation] 组合为 [AnimationSet]，同时播放。
     *
     * 例如同时向左移动 + 淡出：
     * ```kotlin
     * AnimCreator.combine(
     *     TranslateAnimation(0f, -1000f, 0f, 0f),
     *     AlphaAnimation(1f, 0f).apply { fillAfter = true },
     *     duration = 200L
     * )
     * ```
     */
    fun combine(vararg animations: Animation, duration: Long): AnimationSet {
        return AnimationSet(true).apply {
            this.duration = duration
            animations.forEach { addAnimation(it) }
        }
    }

    /**
     * 创建进入动画组：位移 + 淡入同时播放。
     */
    fun enterCombined(
        moveType: AodSettings.Anim.Enter,
        duration: Float,
        withFadeIn: Boolean = true
    ): Animation {
        val moveAnim = enter(moveType, duration)
        if (!withFadeIn || moveType == AodSettings.Anim.Enter.FadeIn) return moveAnim
        return combine(
            moveAnim,
            AlphaAnimation(0f, 1f).apply { fillBefore = true },
            duration = duration.toLong()
        )
    }

    /**
     * 创建退出动画组：位移 + 淡出同时播放。
     */
    fun exitCombined(
        moveType: AodSettings.Anim.Exit,
        duration: Float,
        withFadeOut: Boolean = true
    ): Animation {
        val moveAnim = exit(moveType, duration)
        if (!withFadeOut || moveType == AodSettings.Anim.Exit.FadeOut) return moveAnim
        return combine(
            moveAnim,
            AlphaAnimation(1f, 0f).apply { fillAfter = true },
            duration = duration.toLong()
        )
    }
}