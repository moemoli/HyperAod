package moe.imoli.hyperaod

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.content.edit
import io.github.libxposed.service.RemotePreferences
import moe.imoli.hyperaod.app.HyperAod

/**
 * AOD 全局设置
 *
 * 管理所有 AOD 相关配置项，通过 RemotePreferences 实现 UI 进程与 Hook 进程的配置同步。
 * UI 进程修改设置后调用 [update] 写入远端，Hook 进程通过 [watch] 监听变更并 [reload]。
 */
object AodSettings {

    /** 设置是否已从远端加载完成 */
    @Volatile
    var loaded = false
        private set

    private val onReloaded = mutableListOf<() -> Unit>()

    /** UI 进程注册回调，在 reload 完成后触发 */
    fun addOnReloadedListener(listener: () -> Unit) {
        synchronized(onReloaded) { onReloaded.add(listener) }
    }

    /** 移除已注册的回调 */
    fun removeOnReloadedListener(listener: () -> Unit) {
        synchronized(onReloaded) { onReloaded.remove(listener) }
    }

    /** 一言刷新间隔下限（秒） */
    const val MIN_UPDATE_INTERVAL = 30

    var lyric = LyricSettings
    var hitokoto = HitokotoSettings
    var behavior = BehaviorSettings
    private var update = true

    /** 运行时状态：歌词是否正在播放（由 LyricModifier 更新） */
    @Volatile
    var lyricPlaying: Boolean = false

    /**
     * 歌词相关设置
     *
     * 每个 setter 在 [update] 为 true 时自动触发远端同步。
     */
    object LyricSettings {
        var exitAnimDuration: Float = 200f
            set(value) {
                field = value
                if (update) update("lyric.exitAnimDuration", value)
            }

        var enterAnimDuration: Float = 200f
            set(value) {
                field = value
                if (update) update("lyric.enterAnimDuration", value)
            }
        var exitAnim: Anim.Exit = Anim.Exit.None
            set(value) {
                field = value
                if (update) update("lyric.exitAnim", value.type)
            }
        var enterAnim: Anim.Enter = Anim.Enter.None
            set(value) {
                field = value
                if (update) update("lyric.enterAnim", value.type)
            }
        var alignment: Alignment = Alignment.Center
            set(value) {
                field = value
                if (update) update("lyric.alignment", value.type)
            }
        var marginRight: Float = 0f
            set(value) {
                field = value
                if (update) update("lyric.marginRight", value)
            }
        var marginLeft: Float = 0f
            set(value) {
                field = value
                if (update) update("lyric.marginLeft", value)
            }
        var marginBottom: Float = 0f
            set(value) {
                field = value
                if (update) update("lyric.marginBottom", value)
            }
        var marginTop: Float = 0f
            set(value) {
                field = value
                if (update) update("lyric.marginTop", value)
            }

        /** 字体颜色，ARGB long 格式 */
        var fontColor: Long = -1L
            set(value) {
                field = value
                if (update) update("lyric.fontColor", value)
            }

        /** 字体大小，单位 sp */
        var fontSize: Float = 16f
            set(value) {
                field = value
                if (update) update("lyric.fontSize", value)
            }

        /** 是否启用歌词显示 */
        var enable = false
            set(value) {
                field = value
                if (update) update("lyric.enable", value)
            }

        fun updateAlignment(type: Int) {
            alignment = Alignment.valueOf(type)
        }

        fun updateEnterAnim(type: Int) {
            enterAnim = Anim.Enter.valueOf(type)
        }

        fun updateExitAnim(type: Int) {
            exitAnim = Anim.Exit.valueOf(type)
        }

        override fun toString(): String {
            return "enable=;" +
                    "fontSize=;" +
                    "fontColor=0x;" +
                    "marginTop=;" +
                    "marginBottom=;" +
                    "marginLeft=;" +
                    "marginRight=;" +
                    "alignment=;" +
                    "enterAnim=;" +
                    "enterAnimDuration=;" +
                    "exitAnim=;" +
                    "exitAnimDuration=;" +
                    "updateInterval=;" +
                    "exitAnim=;" +
                    "exitAnimDuration=;"
        }
    }


    /**
     * 一言展示设置
     *
     * 在 AOD 上显示一言文本（来自 hitokoto.cn），支持退出动画和定时刷新。
     * 每个 setter 在 [update] 为 true 时自动触发远端同步。
     */
    object HitokotoSettings {
        /** 是否启用一言显示 */
        var enable: Boolean = false
            set(value) {
                field = value
                if (update) update("hitokoto.enable", value)
            }

        /** 字体大小，单位 sp */
        var fontSize: Float = 16f
            set(value) {
                field = value
                if (update) update("hitokoto.fontSize", value)
            }

        /** 字体颜色，ARGB long 格式 */
        var fontColor: Long = -1L
            set(value) {
                field = value
                if (update) update("hitokoto.fontColor", value)
            }
        var marginTop: Float = 0f
            set(value) {
                field = value
                if (update) update("hitokoto.marginTop", value)
            }
        var marginBottom: Float = 0f
            set(value) {
                field = value
                if (update) update("hitokoto.marginBottom", value)
            }
        var marginLeft: Float = 0f
            set(value) {
                field = value
                if (update) update("hitokoto.marginLeft", value)
            }
        var marginRight: Float = 0f
            set(value) {
                field = value
                if (update) update("hitokoto.marginRight", value)
            }
        var alignment: Alignment = Alignment.Center
            set(value) {
                field = value
                if (update) update("hitokoto.alignment", value.type)
            }
        var enterAnim: Anim.Enter = Anim.Enter.None
            set(value) {
                field = value
                if (update) update("hitokoto.enterAnim", value.type)
            }
        var enterAnimDuration: Float = 200f
            set(value) {
                field = value
                if (update) update("hitokoto.enterAnimDuration", value)
            }
        var exitAnim: Anim.Exit = Anim.Exit.None
            set(value) {
                field = value
                if (update) update("hitokoto.exitAnim", value.type)
            }
        var exitAnimDuration: Float = 200f
            set(value) {
                field = value
                if (update) update("hitokoto.exitAnimDuration", value)
            }

        /**
         * 一言内容自动刷新间隔，单位秒，最低 30 秒。
         */
        var updateInterval: Int = 60
            set(value) {
                field = value.coerceAtLeast(MIN_UPDATE_INTERVAL)
                if (update) update("hitokoto.updateInterval", value)
            }

        /**
         * 句子类型筛选集合。
         * 对应 hitokoto.cn API 的 c 参数，值为类型代码（如 "a","b","c"）。
         * 空集合表示不筛选（请求所有类型）。
         */
        var sentenceTypes: MutableSet<String> = mutableSetOf()
            set(value) {
                field = value
                if (update) update("hitokoto.sentenceTypes", value.joinToString(","))
            }

        /** 最小字数，0 表示不限制 */
        var minLength: Int = 0
            set(value) {
                field = value
                if (update) update("hitokoto.minLength", value)
            }

        /** 最大字数，0 表示不限制 */
        var maxLength: Int = 0
            set(value) {
                field = value
                if (update) update("hitokoto.maxLength", value)
            }

        fun updateAlignment(type: Int) {
            alignment = Alignment.valueOf(type)
        }

        fun updateEnterAnim(type: Int) {
            enterAnim = Anim.Enter.valueOf(type)
        }

        fun updateExitAnim(type: Int) {
            exitAnim = Anim.Exit.valueOf(type)
        }

        override fun toString(): String {
            return "enable=;" +
                    "fontSize=;" +
                    "fontColor=0x;" +
                    "marginTop=;" +
                    "marginBottom=;" +
                    "marginLeft=;" +
                    "marginRight=;" +
                    "alignment=;" +
                    "enterAnim=;" +
                    "enterAnimDuration=;" +
                    "exitAnim=;" +
                    "exitAnimDuration=;" +
                    "updateInterval=;"
        }
    }

    /** 将当前设置写入远端 SharedPreferences */
    fun <T> update(key: String, type: T) {
        HyperAod.SERVICE?.getRemotePreferences("hook")?.edit {
            // lyric
            when (type) {
                is Boolean -> {
                    putBoolean(key, type)
                }

                is Float -> {
                    putFloat(key, type)
                }

                is Long -> {
                    putLong(key, type)
                }

                is String -> {
                    putString(key, type)
                }

                is Int -> {
                    putInt(key, type)
                }

                else -> {
                    Log.d(ModuleMain.TAG, "unknown type $type for key $key")
                }

            }
            apply()
        } ?: Log.d(ModuleMain.TAG, "remote null")
    }

    /** 从 SharedPreferences 重新加载所有设置 */
    fun reload(prefs: SharedPreferences, key: String? = null) {
        update = false

        // lyric
        if (key == null || key == "lyric.enable")
            lyric.enable = prefs.getBoolean("lyric.enable", lyric.enable)
        if (key == null || key == "lyric.fontSize")
            lyric.fontSize = prefs.getFloat("lyric.fontSize", lyric.fontSize)
        if (key == null || key == "lyric.fontColor")
            lyric.fontColor = prefs.getLong("lyric.fontColor", -1L)
        if (key == null || key == "lyric.marginTop")
            lyric.marginTop = prefs.getFloat("lyric.marginTop", lyric.marginTop)
        if (key == null || key == "lyric.marginBottom")
            lyric.marginBottom = prefs.getFloat("lyric.marginBottom", lyric.marginBottom)
        if (key == null || key == "lyric.marginLeft")
            lyric.marginLeft = prefs.getFloat("lyric.marginLeft", lyric.marginLeft)
        if (key == null || key == "lyric.marginRight")
            lyric.marginRight = prefs.getFloat("lyric.marginRight", lyric.marginRight)
        if (key == null || key == "lyric.enterAnimDuration")
            lyric.enterAnimDuration =
                prefs.getFloat("lyric.enterAnimDuration", lyric.enterAnimDuration)
        if (key == null || key == "lyric.exitAnimDuration")
            lyric.exitAnimDuration =
                prefs.getFloat("lyric.exitAnimDuration", lyric.exitAnimDuration)
        if (key == null || key == "lyric.enterAnim")
            lyric.enterAnim =
                Anim.Enter.valueOf(prefs.getInt("lyric.enterAnim", lyric.enterAnim.type))
        if (key == null || key == "lyric.exitAnim")
            lyric.exitAnim = Anim.Exit.valueOf(prefs.getInt("lyric.exitAnim", lyric.exitAnim.type))
        if (key == null || key == "lyric.alignment")
            lyric.alignment =
                Alignment.valueOf(prefs.getInt("lyric.alignment", lyric.alignment.type))

        // hitokoto
        if (key == null || key == "hitokoto.enable")
            hitokoto.enable = prefs.getBoolean("hitokoto.enable", hitokoto.enable)
        if (key == null || key == "hitokoto.fontSize")
            hitokoto.fontSize = prefs.getFloat("hitokoto.fontSize", hitokoto.fontSize)
        if (key == null || key == "hitokoto.fontColor")
            hitokoto.fontColor = prefs.getLong("hitokoto.fontColor", -1L)
        if (key == null || key == "hitokoto.marginTop")
            hitokoto.marginTop = prefs.getFloat("hitokoto.marginTop", hitokoto.marginTop)
        if (key == null || key == "hitokoto.marginBottom")
            hitokoto.marginBottom = prefs.getFloat("hitokoto.marginBottom", hitokoto.marginBottom)
        if (key == null || key == "hitokoto.marginLeft")
            hitokoto.marginLeft = prefs.getFloat("hitokoto.marginLeft", hitokoto.marginLeft)
        if (key == null || key == "hitokoto.marginRight")
            hitokoto.marginRight = prefs.getFloat("hitokoto.marginRight", hitokoto.marginRight)
        if (key == null || key == "hitokoto.enterAnimDuration")
            hitokoto.enterAnimDuration =
                prefs.getFloat("hitokoto.enterAnimDuration", hitokoto.enterAnimDuration)
        if (key == null || key == "hitokoto.enterAnim")
            hitokoto.enterAnim =
                Anim.Enter.valueOf(prefs.getInt("hitokoto.enterAnim", hitokoto.enterAnim.type))
        if (key == null || key == "hitokoto.alignment")
            hitokoto.alignment =
                Alignment.valueOf(prefs.getInt("hitokoto.alignment", hitokoto.alignment.type))
        if (key == null || key == "hitokoto.exitAnim")
            hitokoto.exitAnim =
                Anim.Exit.valueOf(prefs.getInt("hitokoto.exitAnim", hitokoto.exitAnim.type))
        if (key == null || key == "hitokoto.exitAnimDuration")
            hitokoto.exitAnimDuration =
                prefs.getFloat("hitokoto.exitAnimDuration", hitokoto.exitAnimDuration)
        if (key == null || key == "hitokoto.updateInterval")
            hitokoto.updateInterval =
                prefs.getInt("hitokoto.updateInterval", hitokoto.updateInterval)
        if (key == null || key == "hitokoto.sentenceTypes") {
            val storedTypes = prefs.getString("hitokoto.sentenceTypes", "") ?: ""
            hitokoto.sentenceTypes = if (storedTypes.isEmpty()) mutableSetOf()
            else storedTypes.split(",").toMutableSet()
        }
        if (key == null || key == "hitokoto.minLength")
            hitokoto.minLength = prefs.getInt("hitokoto.minLength", hitokoto.minLength)
        if (key == null || key == "hitokoto.maxLength")
            hitokoto.maxLength = prefs.getInt("hitokoto.maxLength", hitokoto.maxLength)

        // behavior
        if (key == null || key == "behavior.hideHitokotoWhenLyric")
            behavior.hideHitokotoWhenLyric =
                prefs.getBoolean("behavior.hideHitokotoWhenLyric", behavior.hideHitokotoWhenLyric)

        if (key == null || key == "behavior.displayOrder") {
            val storedOrder = prefs.getString("behavior.displayOrder", "") ?: ""
            if (storedOrder.isNotEmpty()) {
                behavior.displayOrder = storedOrder.split(",").toMutableList()
            }
        }


        update = true
        loaded = true

        Log.d(ModuleMain.TAG, "reload success: ")

        synchronized(onReloaded) { onReloaded.forEach { it() } }
    }

    override fun toString(): String {
        return "LyricSetting=(), HitokotoSetting=()"
    }

    /** 注册远端 SharedPreferences 变更监听 */
    fun watch(remotePreferences: SharedPreferences) {
        remotePreferences.registerOnSharedPreferenceChangeListener { _, key ->
            Log.d(ModuleMain.TAG, "watch change: $key")
            reload(remotePreferences, key)
        }
    }

    /**
     * 行为设置
     *
     * 控制各模块之间的交互行为。
     */
    object BehaviorSettings {
        /** 歌词播放时是否隐藏一言 */
        var hideHitokotoWhenLyric: Boolean = false
            set(value) {
                field = value
                if (update) update("behavior.hideHitokotoWhenLyric", value)
            }

        /**
         * 歌词与一言的显示顺序。
         * 列表第一项先添加到视图（底层），第二项后添加（顶层）。
         * 仅在 [hideHitokotoWhenLyric] 为 false 时生效。
         * 值为 modifier 标识："lyric"、"hitokoto"。
         */
        var displayOrder: MutableList<String> = mutableListOf("lyric", "hitokoto")
            set(value) {
                field = value
                if (update) update("behavior.displayOrder", value.joinToString(","))
            }

        override fun toString(): String {
            return "hideHitokotoWhenLyric=;displayOrder="
        }
    }

    /** 歌词对齐方式 */
    enum class Alignment(val type: Int) {
        Center(0),
        Left(1),
        Right(2), ;

        companion object {
            fun valueOf(value: Int): Alignment = when (value) {
                0 -> Center
                1 -> Left
                2 -> Right
                else -> Center
            }
        }
    }

    /** 动画类型定义 */
    object Anim {
        /** 进入动画 */
        enum class Enter(val type: Int) {
            None(0),
            FadeIn(1),
            Up(2),
            Down(3), ;

            companion object {
                fun valueOf(value: Int): Enter = when (value) {
                    0 -> None
                    1 -> FadeIn
                    2 -> Up
                    3 -> Down
                    else -> None
                }
            }
        }

        /** 退出动画 */
        enum class Exit(val type: Int) {
            None(0),
            FadeOut(1),
            Up(2),
            Down(3), ;

            companion object {
                fun valueOf(value: Int): Exit = when (value) {
                    0 -> None
                    1 -> FadeOut
                    2 -> Up
                    3 -> Down
                    else -> None
                }
            }
        }
    }
}