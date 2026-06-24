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
 * 文本在 [prefetch] 时预取并缓存，AOD 显示时直接使用缓存避免延迟。
 * 按 [AodSettings.HitokotoSettings.updateInterval] 间隔自动刷新内容。
 *
 * 当 [AodSettings.BehaviorSettings.hideHitokotoWhenLyric] 为 true 时，
 * 与 [LyricModifier] 共享 [SharedTextSwitcher]，切换时使用对应来源的动画。
 */
object HitokotoModifier : AodModifier() {

    private var hitokotoSwitcher: HitokotoSwitcher? = null
    private var initialized = false
    private var container: FrameLayout? = null
    private var dozeHost: Any? = null

    @Volatile
    private var cachedText: String? = null

    @Volatile
    private var refreshScheduled = false

    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = Runnable { onRefresh() }

    /** 是否使用共享 TextSwitcher */
    private val useShared get() = AodSettings.behavior.hideHitokotoWhenLyric

    fun prefetch() {
        if (!AodSettings.hitokoto.enable) return
        fetchAndCache()
        startPeriodicRefresh()
    }

    private fun fetchAndCache() {
        Thread {
            try {
                val url = buildRequestUrl()
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto fetch: $url")
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
                cachedText = if (from.isNotEmpty()) "$hitokotoText ——$from" else hitokotoText
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto cached: $cachedText")
            } catch (e: Exception) {
                Log.e(ModuleMain.TAG, "Failed to fetch hitokoto", e)
            }
        }.start()
    }

    private fun onRefresh() {
        Thread {
            try {
                val url = buildRequestUrl()
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto refresh: $url")
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
                val text = if (from.isNotEmpty()) "$hitokotoText ——$from" else hitokotoText
                cachedText = text
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto refreshed: $text")
                if (initialized) {
                    val c = container ?: return@Thread
                    val dh = dozeHost ?: return@Thread
                    c.post {
                        refresh(c, dh)
                        if (useShared) {
                            SharedTextSwitcher.showHitokoto(text)
                        } else {
                            hitokotoSwitcher?.update(text)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(ModuleMain.TAG, "Failed to refresh hitokoto", e)
            }
        }.start()
        scheduleNextRefresh()
    }

    private fun startPeriodicRefresh() {
        if (refreshScheduled) return
        refreshScheduled = true
        scheduleNextRefresh()
    }

    private fun scheduleNextRefresh() {
        val intervalMs = AodSettings.hitokoto.updateInterval.toLong() * 1000L
        handler.postDelayed(refreshRunnable, intervalMs)
        if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto next refresh in ${AodSettings.hitokoto.updateInterval}s")
    }

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

        if (useShared) {
            // 共享模式：不创建自己的视图，使用 SharedTextSwitcher
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto using SharedTextSwitcher")
        } else {
            // 独立模式：创建自己的 HitokotoSwitcher
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
        }

        // 使用预取缓存
        val cached = cachedText
        if (cached != null) {
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto using cached: $cached")
            container?.post {
                refresh(container, dozeHost)
                if (useShared) {
                    SharedTextSwitcher.showHitokoto(cached)
                } else {
                    hitokotoSwitcher?.update(cached)
                }
            }
        }

        initialized = true
    }

    override fun update() {
        if (!initialized) return
        handler.removeCallbacks(refreshRunnable)
        onRefresh()
    }

    /**
     * 更新一言可见性。
     *
     * 共享模式下仅管理定时刷新，不操作视图显隐（由 SharedTextSwitcher 管理切换）。
     * 独立模式下直接隐藏/显示一言视图。
     */
    /**
     * 在共享模式下恢复一言显示。
     *
     * 由 [LyricModifier] 在歌词停止时调用，
     * 使用缓存文本通过 [SharedTextSwitcher] 显示一言。
     * 若缓存为空则触发一次新的获取。
     */
    fun restoreInShared() {
        val cached = cachedText
        if (cached != null) {
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto restoreInShared: ")
            SharedTextSwitcher.showHitokoto(cached)
        } else {
            fetchAndDisplayInShared()
        }
        startPeriodicRefresh()
    }

    /**
     * 获取一言文本并直接在 SharedTextSwitcher 中显示（不依赖 initialized）。
     */
    private fun fetchAndDisplayInShared() {
        Thread {
            try {
                val url = buildRequestUrl()
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto fetch for shared: ")
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
                val text = if (from.isNotEmpty()) " ——" else hitokotoText
                cachedText = text
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto fetched for shared: ")
                Handler(Looper.getMainLooper()).post {
                    SharedTextSwitcher.showHitokoto(text)
                }
            } catch (e: Exception) {
                Log.e(ModuleMain.TAG, "Failed to fetch hitokoto for shared", e)
            }
        }.start()
    }

    fun updateVisibility() {
        if (!initialized) return
        val shouldHide = AodSettings.behavior.hideHitokotoWhenLyric && AodSettings.lyricPlaying
        if (useShared) {
            // 共享模式：只管理定时刷新，不动视图
            if (shouldHide) {
                handler.removeCallbacks(refreshRunnable)
                refreshScheduled = false
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto refresh paused: lyric playing")
            } else {
                startPeriodicRefresh()
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto refresh resumed")
            }
        } else {
            // 独立模式：管理视图显隐
            val view = hitokotoSwitcher?.view ?: return
            if (shouldHide) {
                handler.removeCallbacks(refreshRunnable)
                refreshScheduled = false
                view.visibility = View.GONE
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto hidden: lyric playing")
            } else {
                view.visibility = View.VISIBLE
                startPeriodicRefresh()
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto shown")
            }
        }
    }

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
        refreshScheduled = false
        if (!useShared) {
            // 独立模式：移除自己的视图
            hitokotoSwitcher?.view?.let { view ->
                (view.parent as? ViewGroup)?.removeView(view)
            }
        }
        // 共享模式：SharedTextSwitcher 由 LyricModifier.close() 负责释放
        hitokotoSwitcher = null
        container = null
        dozeHost = null
        initialized = false
    }

    /**
     * 一言文本切换器（独立模式使用）
     */
    class HitokotoSwitcher {
        lateinit var view: TextSwitcher

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