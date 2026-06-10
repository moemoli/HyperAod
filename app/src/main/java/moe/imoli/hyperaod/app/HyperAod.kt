package moe.imoli.hyperaod.app

import android.app.Application
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import moe.imoli.hyperaod.AodSettings

class HyperAod : Application(), XposedServiceHelper.OnServiceListener {

    companion object {

        lateinit var APP: Application
        var SERVICE: XposedService? = null
    }


    override fun onCreate() {
        super.onCreate()
        APP = this
        XposedServiceHelper.registerListener(this)
    }

    override fun onServiceBind(service: XposedService) {
        SERVICE = service
        val prefs = service.getRemotePreferences("hook")
        AodSettings.reload(prefs)
    }

    override fun onServiceDied(service: XposedService) {
        SERVICE = null
    }
}