package moe.imoli.hyperaod.aod

import android.content.Context
import android.widget.FrameLayout

object ModifierManager {
    fun update() {
        DebugModifier.update()
    }

    fun init(mAodView: FrameLayout?, mContainer: FrameLayout?, mContext: Context, thisObject: Any) {
        DebugModifier.init(mAodView, mContainer, mContext, thisObject)
    }

    fun close() {
        DebugModifier.close()
    }

}