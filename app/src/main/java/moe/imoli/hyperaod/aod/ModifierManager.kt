package moe.imoli.hyperaod.aod

import android.content.Context
import android.widget.FrameLayout

object ModifierManager {

    val modifiers = arrayOf(
        LyricModifier
    )

    fun update() {
        modifiers.forEach { modifier -> modifier.update() }
    }

    fun init(mAodView: FrameLayout?, mContainer: FrameLayout?, mContext: Context, thisObject: Any) {
        modifiers.forEach { modifier -> modifier.init(mAodView, mContainer, mContext, thisObject) }
    }

    fun close() {
        modifiers.forEach { modifier -> modifier.close() }
    }

}