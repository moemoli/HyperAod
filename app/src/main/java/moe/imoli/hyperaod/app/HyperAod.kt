package moe.imoli.hyperaod.app

import android.app.Application
import android.content.Context
import moe.imoli.hyperaod.AodSettings

class HyperAod : Application() {

    companion object {

        lateinit var APP: Application
    }


    override fun onCreate() {
        super.onCreate()
        APP = this
        val prefs = APP.getSharedPreferences("hook", MODE_PRIVATE)
        AodSettings.reload(prefs)
    }
}