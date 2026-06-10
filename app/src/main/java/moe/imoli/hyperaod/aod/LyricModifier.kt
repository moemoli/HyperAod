package moe.imoli.hyperaod.aod

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.TextSwitcher
import android.widget.TextView
import com.hchen.superlyricapi.ISuperLyricReceiver
import com.hchen.superlyricapi.SuperLyricData
import com.hchen.superlyricapi.SuperLyricHelper
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import moe.imoli.hyperaod.AodSettings
import moe.imoli.hyperaod.AodSettings.Alignment.*
import moe.imoli.hyperaod.ModuleMain
import moe.imoli.hyperaod.ui.anim.AnimCreator

object LyricModifier : AodModifier() {


    private var receiver: ISuperLyricReceiver.Stub? = null


    override fun init(
        aodView: FrameLayout?,
        container: FrameLayout?,
        context: Context,
        dozeHost: Any
    ) {
        if (!AodSettings.lyric.enable) return
        // 歌词样式初始化
        val lyricSwitcher = LyricSwitcher()
        lyricSwitcher.init(context)
        container?.apply {
            val aodContent = container.asResolver()
                .firstField { name = "mAodContent" }
                .get() as ViewGroup
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "add lyricSwitcher")
            aodContent.addView(lyricSwitcher.lyric)
        }

        // 歌词接收
        receiver = object : ISuperLyricReceiver.Stub() {
            override fun onLyric(publisher: String?, data: SuperLyricData?) {
                if (data?.hasLyric() ?: return) {
                    val text = data.lyric?.text ?: return
                    container?.post {
                        if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "receive lyric: $text")
                        refresh(container, dozeHost)
                        lyricSwitcher.update(text)
                    }
                }

            }

            override fun onStop(publisher: String?, data: SuperLyricData?) {
                container?.post {
                    refresh(container, dozeHost)
                    lyricSwitcher.update("")
                }
            }
        }.apply {
            if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "register receiver")
            SuperLyricHelper.registerReceiver(this)
        }
    }

    override fun update() {


    }

    override fun close() {
        receiver?.let {
            if (SuperLyricHelper.isReceiverRegistered(it)) {
                if (ModuleMain.DEBUG) Log.d(ModuleMain.TAG, "unregister receiver")
                SuperLyricHelper.unregisterReceiver(it)
            }
        }
        receiver = null
    }

    class LyricSwitcher {
        lateinit var lyric: TextSwitcher


        fun init(context: Context) {
            // 歌词样式初始化
            lyric = TextSwitcher(context)
            lyric.setFactory {
                TextView(context).apply {
                    // alignemt
                    gravity = when (AodSettings.lyric.alignment) {
                        Center -> Gravity.CENTER
                        Left -> Gravity.START
                        Right -> Gravity.END
                    }

                    // font size
                    textSize = AodSettings.lyric.fontSize
                    // font color
                    setTextColor(AodSettings.lyric.fontColor.toInt())

                }
            }
            lyric.inAnimation =
                AnimCreator.enter(AodSettings.lyric.enterAnim, AodSettings.lyric.enterAnimDuration)
            lyric.outAnimation =
                AnimCreator.exit(AodSettings.lyric.exitAnim, AodSettings.lyric.exitAnimDuration)

            lyric.layoutParams =
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(
                        AodSettings.lyric.marginLeft.toInt(),
                        AodSettings.lyric.marginTop.toInt(),
                        AodSettings.lyric.marginRight.toInt(),
                        AodSettings.lyric.marginBottom.toInt()
                    )
                }

        }

        fun update(text: CharSequence) {
            lyric.setText(text)
            lyric.invalidate()
            lyric.requestLayout()
        }

    }
}