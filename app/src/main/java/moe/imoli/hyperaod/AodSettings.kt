package moe.imoli.hyperaod

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.content.edit
import io.github.libxposed.service.RemotePreferences
import moe.imoli.hyperaod.app.HyperAod

object AodSettings {

    /** 设置是否已从远程加载完成 */
    @Volatile
    var loaded = false
        private set

    private val onReloaded = mutableListOf<() -> Unit>()

    /** UI 进程注册回调，在 reload 完成后触发 */
    fun addOnReloadedListener(listener: () -> Unit) {
        synchronized(onReloaded) { onReloaded.add(listener) }
    }

    var lyric = LyricSettings
    private var update = true

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
        var fontColor: Long = android.graphics.Color.WHITE.toLong()
            set(value) {
                field = value
                if (update) update()
            }
        var fontSize: Float = 16f
            set(value) {
                field = value
                if (update) update()
            }
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
            return "enable=$enable;" +
                    "fontSize=$fontSize;" +
                    "fontColor=0x${fontColor.toHexString()};" +
                    "marginTop=$marginTop;" +
                    "marginBottom=$marginBottom;" +
                    "marginLeft=$marginLeft;" +
                    "marginRight=$marginRight;" +
                    "alignment=$alignment;" +
                    "enterAnim=$enterAnim;" +
                    "enterAnimDuration=$enterAnimDuration;" +
                    "exitAnim=$exitAnim;" +
                    "exitAnimDuration=$exitAnimDuration;"
        }


    }

    fun update() {
        HyperAod.SERVICE?.getRemotePreferences("hook")?.edit {
            // lyric
            putBoolean("lyric.enable", lyric.enable)
            putFloat("lyric.fontSize", lyric.fontSize)
            putLong("lyric.fontColor", lyric.fontColor)
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

            // apply
            apply()
        } ?: Log.d(ModuleMain.TAG, "remote null ")

    }

    fun reload(prefs: SharedPreferences) {
        update = false
        Int.MAX_VALUE

        // lyric
        lyric.enable = prefs.getBoolean("lyric.enable", lyric.enable)
        lyric.fontSize = prefs.getFloat("lyric.fontSize", lyric.fontSize)
        lyric.fontColor = prefs.getLong("lyric.fontColor", lyric.fontColor).takeIf { it != 0L }
            ?: android.graphics.Color.WHITE.toLong()
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

        Log.d(ModuleMain.TAG, "reload success: $this")

        synchronized(onReloaded) { onReloaded.forEach { it() } }
    }

    override fun toString(): String {
        return "LyricSetting=($lyric)"
    }

    fun watch(remotePreferences: SharedPreferences) {
        if (remotePreferences is RemotePreferences) {
            remotePreferences.registerOnSharedPreferenceChangeListener { preferences, string ->
                reload(preferences)
            }
        }
    }


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

    object Anim {
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