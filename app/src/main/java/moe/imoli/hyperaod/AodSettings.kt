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
    private var update = true

    /**
     * 歌词相关设置
     *
     * 每个 setter 在 [update] 为 true 时自动触发远端同步。
     */
    object LyricSettings {
        var exitAnimDuration: Float = 200f
            set(value) {
                field = value
                if (update) update()
            }

        var enterAnimDuration: Float = 200f
            set(value) {
                field = value
                if (update) update()
            }
        var exitAnim: Anim.Exit = Anim.Exit.None
            set(value) {
                field = value
                if (update) update()
            }
        var enterAnim: Anim.Enter = Anim.Enter.None
            set(value) {
                field = value
                if (update) update()
            }
        var alignment: Alignment = Alignment.Center
            set(value) {
                field = value
                if (update) update()
            }
        var marginRight: Float = 0f
            set(value) {
                field = value
                if (update) update()
            }
        var marginLeft: Float = 0f
            set(value) {
                field = value
                if (update) update()
            }
        var marginBottom: Float = 0f
            set(value) {
                field = value
                if (update) update()
            }
        var marginTop: Float = 0f
            set(value) {
                field = value
                if (update) update()
            }
        /** 字体颜色，ARGB long 格式 */
        var fontColor: Long = -1L
            set(value) {
                field = value
                if (update) update()
            }
        /** 字体大小，单位 sp */
        var fontSize: Float = 16f
            set(value) {
                field = value
                if (update) update()
            }
        /** 是否启用歌词显示 */
        var enable = false
            set(value) {
                field = value
                if (update) update()
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
                if (update) update()
            }
        /** 字体大小，单位 sp */
        var fontSize: Float = 16f
            set(value) {
                field = value
                if (update) update()
            }
        /** 字体颜色，ARGB long 格式 */
        var fontColor: Long = -1L
            set(value) {
                field = value
                if (update) update()
            }
        var marginTop: Float = 0f
            set(value) {
                field = value
                if (update) update()
            }
        var marginBottom: Float = 0f
            set(value) {
                field = value
                if (update) update()
            }
        var marginLeft: Float = 0f
            set(value) {
                field = value
                if (update) update()
            }
        var marginRight: Float = 0f
            set(value) {
                field = value
                if (update) update()
            }
        var alignment: Alignment = Alignment.Center
            set(value) {
                field = value
                if (update) update()
            }
        var enterAnim: Anim.Enter = Anim.Enter.None
            set(value) {
                field = value
                if (update) update()
            }
        var enterAnimDuration: Float = 200f
            set(value) {
                field = value
                if (update) update()
            }
        var exitAnim: Anim.Exit = Anim.Exit.None
            set(value) {
                field = value
                if (update) update()
            }
        var exitAnimDuration: Float = 200f
            set(value) {
                field = value
                if (update) update()
            }

        /**
         * 一言内容自动刷新间隔，单位秒，最低 30 秒。
         */
        var updateInterval: Int = 60
            set(value) {
                field = value.coerceAtLeast(MIN_UPDATE_INTERVAL)
                if (update) update()
            }

        /**
         * 句子类型筛选集合。
         * 对应 hitokoto.cn API 的 c 参数，值为类型代码（如 "a","b","c"）。
         * 空集合表示不筛选（请求所有类型）。
         */
        var sentenceTypes: MutableSet<String> = mutableSetOf()
            set(value) {
                field = value
                if (update) update()
            }

        /** 最小字数，0 表示不限制 */
        var minLength: Int = 0
            set(value) {
                field = value
                if (update) update()
            }

        /** 最大字数，0 表示不限制 */
        var maxLength: Int = 0
            set(value) {
                field = value
                if (update) update()
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
    fun update() {
        HyperAod.SERVICE?.getRemotePreferences("hook")?.edit {
            // lyric
            putBoolean("lyric.enable", lyric.enable)
            putFloat("lyric.fontSize", lyric.fontSize)
            putLong("lyric.fontColor", lyric.fontColor.toInt().toLong())
            putFloat("lyric.marginTop", lyric.marginTop)
            putFloat("lyric.marginBottom", lyric.marginBottom)
            putFloat("lyric.marginLeft", lyric.marginLeft)
            putFloat("lyric.marginRight", lyric.marginRight)
            putInt("lyric.alignment", lyric.alignment.type)
            putInt("lyric.enterAnim", lyric.enterAnim.type)
            putInt("lyric.exitAnim", lyric.exitAnim.type)
            putFloat("lyric.enterAnimDuration", lyric.enterAnimDuration)
            putFloat("lyric.exitAnimDuration", lyric.exitAnimDuration)
            // hitokoto
            putBoolean("hitokoto.enable", hitokoto.enable)
            putFloat("hitokoto.fontSize", hitokoto.fontSize)
            putLong("hitokoto.fontColor", hitokoto.fontColor.toInt().toLong())
            putFloat("hitokoto.marginTop", hitokoto.marginTop)
            putFloat("hitokoto.marginBottom", hitokoto.marginBottom)
            putFloat("hitokoto.marginLeft", hitokoto.marginLeft)
            putFloat("hitokoto.marginRight", hitokoto.marginRight)
            putInt("hitokoto.alignment", hitokoto.alignment.type)
            putInt("hitokoto.enterAnim", hitokoto.enterAnim.type)
            putFloat("hitokoto.enterAnimDuration", hitokoto.enterAnimDuration)
            putInt("hitokoto.exitAnim", hitokoto.exitAnim.type)
            putFloat("hitokoto.exitAnimDuration", hitokoto.exitAnimDuration)
            putInt("hitokoto.updateInterval", hitokoto.updateInterval)
            putString("hitokoto.sentenceTypes", hitokoto.sentenceTypes.joinToString(","))
            putInt("hitokoto.minLength", hitokoto.minLength)
            putInt("hitokoto.maxLength", hitokoto.maxLength)

            apply()
        } ?: Log.d(ModuleMain.TAG, "remote null")
    }

    /** 从 SharedPreferences 重新加载所有设置 */
    fun reload(prefs: SharedPreferences) {
        update = false

        // lyric
        lyric.enable = prefs.getBoolean("lyric.enable", lyric.enable)
        lyric.fontSize = prefs.getFloat("lyric.fontSize", lyric.fontSize)
        // 读取颜色值，兼容旧格式（高 32 位）和新格式（低 32 位 ARGB int）
        val storedColor = prefs.getLong("lyric.fontColor", -1L)
        lyric.fontColor = if (storedColor.toInt() == 0 && storedColor != 0L) {
            storedColor shr 32 // 旧格式：颜色在高 32 位，右移得到 ARGB int
        } else {
            storedColor // 新格式：已经是 ARGB int
        }
        lyric.marginTop = prefs.getFloat("lyric.marginTop", lyric.marginTop)
        lyric.marginBottom = prefs.getFloat("lyric.marginBottom", lyric.marginBottom)
        lyric.marginLeft = prefs.getFloat("lyric.marginLeft", lyric.marginLeft)
        lyric.marginRight = prefs.getFloat("lyric.marginRight", lyric.marginRight)
        lyric.enterAnimDuration = prefs.getFloat("lyric.enterAnimDuration", lyric.enterAnimDuration)
        lyric.exitAnimDuration = prefs.getFloat("lyric.exitAnimDuration", lyric.exitAnimDuration)
        lyric.enterAnim = Anim.Enter.valueOf(prefs.getInt("lyric.enterAnim", lyric.enterAnim.type))
        lyric.exitAnim = Anim.Exit.valueOf(prefs.getInt("lyric.exitAnim", lyric.exitAnim.type))
        lyric.alignment = Alignment.valueOf(prefs.getInt("lyric.alignment", lyric.alignment.type))

        // hitokoto
        hitokoto.enable = prefs.getBoolean("hitokoto.enable", hitokoto.enable)
        hitokoto.fontSize = prefs.getFloat("hitokoto.fontSize", hitokoto.fontSize)
        val storedHitokotoColor = prefs.getLong("hitokoto.fontColor", -1L)
        hitokoto.fontColor = if (storedHitokotoColor.toInt() == 0 && storedHitokotoColor != 0L) {
            storedHitokotoColor shr 32
        } else {
            storedHitokotoColor
        }
        hitokoto.marginTop = prefs.getFloat("hitokoto.marginTop", hitokoto.marginTop)
        hitokoto.marginBottom = prefs.getFloat("hitokoto.marginBottom", hitokoto.marginBottom)
        hitokoto.marginLeft = prefs.getFloat("hitokoto.marginLeft", hitokoto.marginLeft)
        hitokoto.marginRight = prefs.getFloat("hitokoto.marginRight", hitokoto.marginRight)
        hitokoto.enterAnimDuration = prefs.getFloat("hitokoto.enterAnimDuration", hitokoto.enterAnimDuration)
        hitokoto.enterAnim = Anim.Enter.valueOf(prefs.getInt("hitokoto.enterAnim", hitokoto.enterAnim.type))
        hitokoto.alignment = Alignment.valueOf(prefs.getInt("hitokoto.alignment", hitokoto.alignment.type))
        hitokoto.exitAnim = Anim.Exit.valueOf(prefs.getInt("hitokoto.exitAnim", hitokoto.exitAnim.type))
        hitokoto.exitAnimDuration = prefs.getFloat("hitokoto.exitAnimDuration", hitokoto.exitAnimDuration)
        hitokoto.updateInterval = prefs.getInt("hitokoto.updateInterval", hitokoto.updateInterval)
        val storedTypes = prefs.getString("hitokoto.sentenceTypes", "") ?: ""
        hitokoto.sentenceTypes = if (storedTypes.isEmpty()) mutableSetOf()
            else storedTypes.split(",").toMutableSet()
        hitokoto.minLength = prefs.getInt("hitokoto.minLength", hitokoto.minLength)
        hitokoto.maxLength = prefs.getInt("hitokoto.maxLength", hitokoto.maxLength)

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
        if (remotePreferences is RemotePreferences) {
            remotePreferences.registerOnSharedPreferenceChangeListener { preferences, _ ->
                reload(preferences)
            }
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
            Down(3),
            Left(4),
            Right(5), ;

            companion object {
                fun valueOf(value: Int): Enter = when (value) {
                    0 -> None
                    1 -> FadeIn
                    2 -> Up
                    3 -> Down
                    4 -> Left
                    5 -> Right
                    else -> None
                }
            }
        }

        /** 退出动画 */
        enum class Exit(val type: Int) {
            None(0),
            FadeOut(1),
            Up(2),
            Down(3),
            Left(4),
            Right(5), ;

            companion object {
                fun valueOf(value: Int): Exit = when (value) {
                    0 -> None
                    1 -> FadeOut
                    2 -> Up
                    3 -> Down
                    4 -> Left
                    5 -> Right
                    else -> None
                }
            }
        }
    }
}