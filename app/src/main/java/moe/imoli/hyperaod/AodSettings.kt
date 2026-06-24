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

    var lyric = LyricSettings
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
                    "exitAnimDuration=;"
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
            // todo...

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

        // todo...
        update = true
        loaded = true

        Log.d(ModuleMain.TAG, "reload success: ")

        synchronized(onReloaded) { onReloaded.forEach { it() } }
    }

    override fun toString(): String {
        return "LyricSetting=()"
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