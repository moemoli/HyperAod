package moe.imoli.hyperaod.ui.anim

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import moe.imoli.hyperaod.AodSettings

/**
 * 动画创建器
 *
 * 根据 [AodSettings.Anim] 配置创建对应的进入/退出动画。
 * 进入动画设置 [Animation.setFillBefore] = true，确保从初始状态开始。
 * 退出动画设置 [Animation.setFillAfter] = true，确保保持结束状态。
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
        AodSettings.Anim.Enter.FadeIn -> AlphaAnimation(0f, 1f)
        AodSettings.Anim.Enter.Up -> TranslateAnimation(0f, 0f, 500f, 0f)
        AodSettings.Anim.Enter.Down -> TranslateAnimation(0f, 0f, -500f, 0f)
    }.apply {
        this.duration = duration.toLong()
        fillBefore = true
    }

    /**
     * 创建退出动画
     *
     * @param type 动画类型
     * @param duration 动画时长（毫秒）
     */
    fun exit(type: AodSettings.Anim.Exit, duration: Float): Animation = when (type) {
        AodSettings.Anim.Exit.None -> TranslateAnimation(0f, 0f, 0f, 0f)
        AodSettings.Anim.Exit.FadeOut -> AlphaAnimation(1f, 0f)
        AodSettings.Anim.Exit.Up -> TranslateAnimation(0f, 0f, 0f, -500f)
        AodSettings.Anim.Exit.Down -> TranslateAnimation(0f, 0f, 0f, 500f)
    }.apply {
        this.duration = duration.toLong()
        fillAfter = true
    }
}