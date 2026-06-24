package moe.imoli.hyperaod.app

import android.app.Application
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import moe.imoli.hyperaod.AodSettings
import java.util.concurrent.CopyOnWriteArraySet

class HyperAod : Application(), XposedServiceHelper.OnServiceListener {

    /**
     * 服务状态监听器，用于 UI 层感知模块激活状态变化。
     */
    interface ServiceStateListener {
        /** 服务状态变化时回调，service 非空表示模块已激活 */
        fun onServiceStateChanged(service: XposedService?)
    }

    companion object {
        lateinit var APP: Application
        var SERVICE: XposedService? = null
            private set

        private val listeners = CopyOnWriteArraySet<ServiceStateListener>()

        fun addServiceStateListener(listener: ServiceStateListener, notifyImmediately: Boolean = false) {
            listeners.add(listener)
            if (notifyImmediately) {
                listener.onServiceStateChanged(SERVICE)
            }
        }

        fun removeServiceStateListener(listener: ServiceStateListener) {
            listeners.remove(listener)
        }

        private fun notifyStateChanged(service: XposedService?) {
            for (listener in listeners) {
                listener.onServiceStateChanged(service)
            }
        }
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
        notifyStateChanged(service)
    }

    override fun onServiceDied(service: XposedService) {
        SERVICE = null
        notifyStateChanged(null)
    }
}