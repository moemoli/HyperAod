package moe.imoli.hyperaod.aod

import android.content.Context
import android.widget.FrameLayout

/**
 * Modifier 管理器
 *
 * 统一管理所有 [AodModifier] 的生命周期：初始化、更新、关闭。
 * 新增 Modifier 时只需添加到 [modifiers] 数组即可。
 */
object ModifierManager {

    /** 已注册的所有 Modifier */
    val modifiers = arrayOf(
        LyricModifier
    )

    /** 通知所有 Modifier 数据更新 */
    fun update() {
        modifiers.forEach { modifier -> modifier.update() }
    }

    /** 初始化所有 Modifier */
    fun init(mAodView: FrameLayout?, mContainer: FrameLayout?, mContext: Context, thisObject: Any) {
        modifiers.forEach { modifier -> modifier.init(mAodView, mContainer, mContext, thisObject) }
    }

    /** 关闭所有 Modifier，释放资源 */
    fun close() {
        modifiers.forEach { modifier -> modifier.close() }
    }
}