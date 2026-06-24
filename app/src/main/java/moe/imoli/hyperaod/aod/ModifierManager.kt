package moe.imoli.hyperaod.aod

import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import moe.imoli.hyperaod.AodSettings
import moe.imoli.hyperaod.ModuleMain

/**
 * Modifier 管理器
 *
 * 统一管理所有 [AodModifier] 的生命周期：初始化、更新、关闭。
 * 初始化顺序由 [AodSettings.BehaviorSettings.displayOrder] 决定，
 * 先添加的 modifier 位于视图底层，后添加的位于顶层。
 */
object ModifierManager {

    /** modifier 标识到实例的映射 */
    private val registry = mapOf<String, AodModifier>(
        "lyric" to LyricModifier,
        "hitokoto" to HitokotoModifier
    )

    /** 通知所有 Modifier 数据更新 */
    fun update() {
        registry.values.forEach { it.update() }
    }

    /**
     * 按 [AodSettings.BehaviorSettings.displayOrder] 顺序初始化所有 Modifier。
     * 排在前面的先添加到容器（底层），排在后面的后添加（顶层）。
     */
    fun init(mAodView: FrameLayout?, mContainer: FrameLayout?, mContext: Context, thisObject: Any) {
        val order = AodSettings.behavior.displayOrder
        if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "ModifierManager init order: $order")
        order.forEach { id ->
            val modifier = registry[id]
            if (modifier != null) {
                modifier.init(mAodView, mContainer, mContext, thisObject)
            } else if (ModuleMain.DEBUG) {
                Log.w(ModuleMain.TAG, "Unknown modifier id: $id")
            }
        }
    }

    /** 关闭所有 Modifier，释放资源 */
    fun close() {
        registry.values.forEach { it.close() }
    }
}