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
                val text = conn.inputStream.bufferedReader().use { it.readText() }.trim()
                conn.disconnect()
                cachedText = text
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto cached: $text")
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
                val text = conn.inputStream.bufferedReader().use { it.readText() }.trim()
                conn.disconnect()
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
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto using SharedTextSwitcher")
        } else {
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

    fun restoreInShared() {
        val cached = cachedText
        if (cached != null) {
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto restoreInShared: $cached")
            SharedTextSwitcher.showHitokoto(cached)
        } else {
            fetchAndDisplayInShared()
        }
        startPeriodicRefresh()
    }

    private fun fetchAndDisplayInShared() {
        Thread {
            try {
                val url = buildRequestUrl()
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto fetch for shared: $url")
                val conn = url.openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.connect()
                val text = conn.inputStream.bufferedReader().use { it.readText() }.trim()
                conn.disconnect()
                cachedText = text
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto fetched for shared: $text")
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
            if (shouldHide) {
                handler.removeCallbacks(refreshRunnable)
                refreshScheduled = false
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto refresh paused: lyric playing")
            } else {
                startPeriodicRefresh()
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "hitokoto refresh resumed")
            }
        } else {
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
        params.add("encode=text")
        sb.append("?")
        sb.append(params.joinToString("&"))
        return java.net.URL(sb.toString())
    }

    override fun close() {
        handler.removeCallbacks(refreshRunnable)
        refreshScheduled = false
        if (!useShared) {
            hitokotoSwitcher?.view?.let { view ->
                (view.parent as? ViewGroup)?.removeView(view)
            }
        }
        hitokotoSwitcher = null
        container = null
        dozeHost = null
        initialized = false
    }

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
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                child.clearAnimation()
                child.alpha = 1f
                child.translationX = 0f
                child.translationY = 0f
            }
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