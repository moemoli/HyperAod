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
import moe.imoli.hyperaod.ModuleMain

object LyricModifier : AodModifier() {


    private var receiver: ISuperLyricReceiver.Stub? = null


    override fun init(
        aodView: FrameLayout?,
        container: FrameLayout?,
        context: Context,
        dozeHost: Any
    ) {

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
                        refresh(container,dozeHost)
                        lyricSwitcher.update(text)

                    }
                }

            }

            override fun onStop(publisher: String?, data: SuperLyricData?) {
                container?.post {
                    refresh(container,dozeHost)
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
                    gravity = Gravity.CENTER
                }
            }
            lyric.inAnimation = TranslateAnimation(0f, 0f, 200f, 0f)
                .apply {
                    duration = 300
                }
            lyric.outAnimation = TranslateAnimation(0f, 0f, 0f, -200f)
                .apply {
                    duration = 300
                }

            lyric.layoutParams =
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)

        }

        fun update(text: CharSequence) {
            lyric.setText(text)
            lyric.invalidate()
            lyric.requestLayout()
        }

    }
}