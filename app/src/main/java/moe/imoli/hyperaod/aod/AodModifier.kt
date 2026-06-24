package moe.imoli.hyperaod.aod

import android.content.Context
import android.widget.FrameLayout
import com.highcapable.kavaref.KavaRef.Companion.asResolver

/**
 * AOD Modifier 抽象基类
 *
 * 负责在息屏 (Always-on Display) 视图上注入自定义内容。
 * 子类通过 [init] 接收 AOD 视图容器，通过 [update] 响应数据变更，
 * 通过 [close] 清理资源。
 */
abstract class AodModifier {

    /**
     * 初始化 Modifier，在 AOD 视图准备好后调用。
     *
     * @param aodView AOD 根视图
     * @param container AOD 内容容器
     * @param context 上下文
     * @param dozeHost DozeHost 实例，用于控制 wakelock 等
     */
    abstract fun init(
        aodView: FrameLayout?,
        container: FrameLayout?,
        context: Context,
        dozeHost: Any
    )

    /** 数据更新时调用，子类刷新 UI */
    abstract fun update()

    /** 关闭 Modifier，释放资源 */
    open fun close() {}

    /**
     * 请求刷新 AOD 视图。
     *
     * 通过反射获取 DozeHost 的 wakelock 并触发 AOD 视图重绘。
     *
     * @param aodView AOD 视图（需要有 updateAodView 方法）
     * @param dozeHost DozeHost 实例（需要有 requestDrawWakelock 方法）
     */
    fun refresh(aodView: Any, dozeHost: Any) {
        dozeHost.asResolver().apply {
            firstMethod { name = "requestDrawWakelock" }
                .invoke(2000L, "Lyric#Update")
        }
        aodView.asResolver().apply {
            firstMethod { name = "updateAodView" }
                .invoke()
        }
    }
}