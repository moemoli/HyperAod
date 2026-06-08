package moe.imoli.hyperaod.aod

import android.content.Context
import android.os.PowerManager
import android.view.View
import android.widget.FrameLayout
import com.highcapable.kavaref.KavaRef.Companion.asResolver

abstract class AodModifier(

) {


    abstract fun init(
        aodView: FrameLayout?,
        container: FrameLayout?,
        context: Context,
        dozeHost: Any
    )

    abstract fun update()

    open fun close() {}

    fun refresh(aodView: Any) {
        aodView.asResolver().apply {
            firstMethod { name = "updateAodView" }
                .invoke()
        }


    }
}