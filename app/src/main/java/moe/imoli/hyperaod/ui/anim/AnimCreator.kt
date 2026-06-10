package moe.imoli.hyperaod.ui.anim

import android.animation.AnimatorSet
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import moe.imoli.hyperaod.AodSettings

object AnimCreator {
    fun enter(type: AodSettings.Anim.Enter, duration: Float): Animation = when (type) {
        AodSettings.Anim.Enter.None -> TranslateAnimation(0f, 0f, 0f, 0f)
        AodSettings.Anim.Enter.FadeIn -> AlphaAnimation(0f, 1f).apply { fillBefore = true }
        AodSettings.Anim.Enter.Up -> TranslateAnimation(0f, 0f, 500f, 0f)
        AodSettings.Anim.Enter.Down -> TranslateAnimation(0f, 0f, -500f, 0f)
        AodSettings.Anim.Enter.Left -> TranslateAnimation(0f, -1000f, 0f, 0f)
        AodSettings.Anim.Enter.Right -> TranslateAnimation(0f, 1000f, 0f, 0f)
    }.apply {
        this.duration = duration.toLong()
    }

    fun exit(type: AodSettings.Anim.Exit, duration: Float): Animation = when (type) {
        AodSettings.Anim.Exit.None -> TranslateAnimation(0f, 0f, 0f, 0f)
        AodSettings.Anim.Exit.FadeOut -> AlphaAnimation(1f, 0f).apply { fillAfter = true }
        AodSettings.Anim.Exit.Up -> TranslateAnimation(0f, 0f, 0f, -500f)
        AodSettings.Anim.Exit.Down -> TranslateAnimation(0f, 0f, 0f, 500f)
        AodSettings.Anim.Exit.Left -> TranslateAnimation(0f, -1000f, 0f, 0f)
        AodSettings.Anim.Exit.Right -> TranslateAnimation(0f, 1000f, 0f, 0f)

    }.apply {
        this.duration = duration.toLong()
    }
}