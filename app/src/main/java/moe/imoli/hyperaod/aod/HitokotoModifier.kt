package moe.imoli.hyperaod.aod

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextSwitcher
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import moe.imoli.hyperaod.AodSettings
import moe.imoli.hyperaod.AodSettings.Alignment.*
import moe.imoli.hyperaod.ModuleMain
import moe.imoli.hyperaod.ui.anim.AnimCreator

/**
 * 一言 Modifier
 *
 * 在 AOD 上显示一言文本（来自 hitokoto.cn），支持进入/退出动画和定时刷新。
 * 通过 [buildRequestUrl] 根据用户配置的句子类型和字数范围构建请求参数，
 * 使用 [TextSwitcher] 实现文本切换动画。
 * 按 [AodSettings.HitokotoSettings.updateInterval] 间隔自动刷新内容。
 */
object HitokotoModifier : AodModifier() {

    private var hitokotoSwitcher: HitokotoSwitcher? = null
    private var initialized = false
    private var container: FrameLayout? = null
    private var dozeHost: Any? = null

    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = Runnable { fetchAndDisplay() }

    override fun init(
        aodView: FrameLayout?,
        container: FrameLayout?,
        context: Context,
        dozeHost: Any
    ) {
        if (!AodSettings.hitokoto.enable) return

        if (initialized) {
            close()
        }

        this.container = container
        this.dozeHost = dozeHost

        val switcher = HitokotoSwitcher()
        switcher.init(context)
        hitokotoSwitcher = switcher

        container?.apply {
            val aodContent = container.asResolver()
                .firstField { name = "mAodContent" }
                .get() as ViewGroup
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "add hitokotoSwitcher")
            aodContent.addView(switcher.view)
            switcher.view.visibility = View.VISIBLE
            switcher.view.bringToFront()
            switcher.view.requestLayout()
        }

        fetchAndDisplay()
        scheduleNextRefresh()

        initialized = true
    }

    override fun update() {
        if (!initialized) return
        handler.removeCallbacks(refreshRunnable)
        fetchAndDisplay()
        scheduleNextRefresh()
    }

    /**
     * 根据歌词播放状态更新一言视图可见性。
     *
     * 当 [AodSettings.BehaviorSettings.hideHitokotoWhenLyric] 为 true 且
     * [AodSettings.lyricPlaying] 为 true 时，隐藏一言视图并暂停定时刷新；
     * 否则恢复显示并重新启动定时刷新。
     *
     * 由 [LyricModifier] 在歌词开始/停止时调用。
     */
    fun updateVisibility() {
        if (!initialized) return
        val shouldHide = AodSettings.behavior.hideHitokotoWhenLyric && AodSettings.lyricPlaying
        val view = hitokotoSwitcher?.view ?: return
        if (shouldHide) {
            handler.removeCallbacks(refreshRunnable)
            view.visibility = View.GONE
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto hidden: lyric playing")
        } else {
            view.visibility = View.VISIBLE
            scheduleNextRefresh()
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto shown")
        }
    }

    /**
     * 按 [AodSettings.HitokotoSettings.updateInterval] 安排下一次刷新。
     */
    private fun scheduleNextRefresh() {
        val intervalMs = AodSettings.hitokoto.updateInterval.toLong() * 1000L
        handler.postDelayed(refreshRunnable, intervalMs)
        if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto next refresh in ${AodSettings.hitokoto.updateInterval}s")
    }

    /**
     * 从 hitokoto.cn API 获取一言文本并更新显示。
     * 在后台线程发起请求，结果通过 post 切回主线程更新 UI。
     */
    private fun fetchAndDisplay() {
        val c = container ?: return
        val dh = dozeHost ?: return
        val switcher = hitokotoSwitcher ?: return
        Thread {
            try {
                val url = buildRequestUrl()
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto request: $url")
                val conn = url.openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.connect()
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()
                val json = org.json.JSONObject(body)
                val hitokotoText = json.optString("hitokoto", "")
                val from = json.optString("from", "")
                val displayText = if (from.isNotEmpty()) "$hitokotoText ——$from" else hitokotoText
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto fetched: $displayText")
                c.post {
                    refresh(c, dh)
                    switcher.update(displayText)
                }
            } catch (e: Exception) {
                Log.e(ModuleMain.TAG, "Failed to fetch hitokoto", e)
            }
        }.start()
    }

    /**
     * 根据 [AodSettings.hitokoto] 构建请求 URL。
     *
     * - sentenceTypes 非空时拼接 c 参数（可多个）
     * - minLength > 0 时拼接 min_length 参数
     * - maxLength > 0 时拼接 max_length 参数
     */
    private fun buildRequestUrl(): java.net.URL {
        val sb = StringBuilder("https://v1.hitokoto.cn/")
        val params = mutableListOf<String>()
        val types = AodSettings.hitokoto.sentenceTypes
        if (types.isNotEmpty()) {
            types.forEach { params.add("c=$it") }
        }
        if (AodSettings.hitokoto.minLength > 0) {
            params.add("min_length=${AodSettings.hitokoto.minLength}")
        }
        if (AodSettings.hitokoto.maxLength > 0) {
            params.add("max_length=${AodSettings.hitokoto.maxLength}")
        }
        if (params.isNotEmpty()) {
            sb.append("?")
            sb.append(params.joinToString("&"))
        }
        return java.net.URL(sb.toString())
    }

    override fun close() {
        handler.removeCallbacks(refreshRunnable)
        hitokotoSwitcher?.view?.let { view ->
            (view.parent as? ViewGroup)?.removeView(view)
        }
        hitokotoSwitcher = null
        container = null
        dozeHost = null
        initialized = false
    }

    /**
     * 一言文本切换器
     *
     * 封装 [TextSwitcher] 的创建和更新逻辑，根据 [AodSettings.hitokoto] 配置样式。
     */
    class HitokotoSwitcher {
        lateinit var view: TextSwitcher

        /**
         * 初始化 TextSwitcher，根据当前设置配置进入/退出动画。
         */
        fun init(context: Context) {
            view = TextSwitcher(context)
            view.setFactory {
                TextView(context).apply {
                    gravity = when (AodSettings.hitokoto.alignment) {
                        Center -> Gravity.CENTER
                        Left -> Gravity.START
                        Right -> Gravity.END
                    }

                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )

                    textSize = AodSettings.hitokoto.fontSize

                    val color = AodSettings.hitokoto.fontColor.toInt()
                    setTextColor(color)
                    if (ModuleMain.DEBUG) Log.d(
                        ModuleMain.TAG,
                        "Hitokoto TextView created: color=0x${color.toHexString()}, size=${textSize}"
                    )
                }
            }

            view.inAnimation =
                AnimCreator.enter(AodSettings.hitokoto.enterAnim, AodSettings.hitokoto.enterAnimDuration)
            view.outAnimation =
                AnimCreator.exit(AodSettings.hitokoto.exitAnim, AodSettings.hitokoto.exitAnimDuration)

            view.layoutParams =
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(
                        AodSettings.hitokoto.marginLeft.toInt(),
                        AodSettings.hitokoto.marginTop.toInt(),
                        AodSettings.hitokoto.marginRight.toInt(),
                        AodSettings.hitokoto.marginBottom.toInt()
                    )
                }
        }

        /**
         * 更新一言文本。
         *
         * @param text 要显示的一言文本，空字符串表示清除
         */
        fun update(text: CharSequence) {
            view.setText(text)
            view.invalidate()
            view.requestLayout()
            if (ModuleMain.DEBUG) {
                val tv = view.currentView as? TextView
                Log.d(
                    ModuleMain.TAG, "hitokoto update: text=\"$text\", tv.text=\"${tv?.text}\", " +
                            "w=${view.width}, h=${view.height}, vis=${view.visibility}"
                )
            }
        }
    }
}