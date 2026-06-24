package moe.imoli.hyperaod

import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.ClassLoaderProvider
import com.highcapable.kavaref.extension.toClassOrNull
import io.github.kyuubiran.ezxhelper.core.EzXReflection
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.-Static.createAfterHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import moe.imoli.hyperaod.aod.ModifierManager

/**
 * Xposed 模块入口
 *
 * Hook SystemUI 的 DozeServicePluginImpl，在 AOD 显示时注入自定义 Modifier。
 */
class ModuleMain : XposedModule() {

    companion object {
        const val TAG = "HyperAod"
        private const val SYSTEMUI_PKG = "com.android.systemui"

        private const val DOZE_SERVICE = "com.android.systemui.shared.plugins.PluginInstance"
        private const val DOZE_PLUGIN_IMPL = "com.miui.aod.doze.DozeServicePluginImpl"
        private const val DOZE_HOST = "com.miui.aod.DozeHost"
        private const val FIELD_M_DOZE_PLUGIN = "mPlugin"
        private const val FIELD_S_HOST = "sHost"

        var DEBUG = BuildConfig.DEBUG
    }

    /** 是否已完成首次初始化 */
    private var init = false

    override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        EzXposed.initOnModuleLoaded(this, param)
    }

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        EzXposed.initOnPackageLoaded(param)
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        EzXposed.initOnPackageReady(param)
        setPluginClassLoader(param.classLoader)
        if (param.packageName != SYSTEMUI_PKG) return
        log(Log.DEBUG, TAG, "try load for package ")

        val dozeClazz = DOZE_SERVICE.toClassOrNull() ?: run {
            log(Log.ERROR, TAG, "DozeService class not found")
            return
        }

        val resolved = dozeClazz.resolve()
        log(Log.DEBUG, TAG, "try find plugin service")

        // Plugin 连接时：拿到 DozePlugin 并初始化 plugin 的 classloader + hook DozeServicePluginImpl
        resolved.lastMethod {
            name = "loadPlugin"
        }
            .self
            .createAfterHook { hookParam ->
                val hostInstance = hookParam.thisObject
                val pluginInstance = resolved.firstField { name = FIELD_M_DOZE_PLUGIN }
                    .of(hostInstance)
                    .get()

                if (pluginInstance == null) {
                    log(Log.ERROR, TAG, "Failed to initialize DozePlugin")
                    return@createAfterHook
                }

                if (init) return@createAfterHook
                log(Log.DEBUG, TAG, "plugin connected, next.")
                setPluginClassLoader(pluginInstance.javaClass.classLoader ?: return@createAfterHook)
                init = true
                AodSettings.reload(getRemotePreferences("hook"))
                AodSettings.watch(getRemotePreferences("hook"))
                DOZE_HOST.toClassOrNull()?.let {
                    it.resolve().apply {
                        // 视图更新时触发（当前未启用）
                        firstMethod { name = "dealWithChange" }.self.createAfterHook {
                            // ModifierManager.update()
                        }
                        // 销毁时触发
                        firstMethod { name = "destroy" }.self.createAfterHook {
                            ModifierManager.close()
                        }
                        // AOD 显示时触发
                        firstMethod {
                            name = "prepareAodViewAndShow"
                            parameterCount = 0
                        }.self.createAfterHook {
                            log(Log.DEBUG, TAG, "try prepare aod view")
                            val mAodView =
                                firstField { name = "mAODView" }.of(it.thisObject)
                                    .get() as? FrameLayout?
                            val mContainer =
                                firstField { name = "mContainer" }.of(it.thisObject)
                                    .get() as? FrameLayout?
                            val mContext =
                                firstField { name = "mContext" }.of(it.thisObject).get() as Context
                            ModifierManager.init(mAodView, mContainer, mContext, it.thisObject)
                        }
                    }
                }
            }

        // Plugin 断开时：还原 classloader
        resolved.firstMethod { name = "unloadPlugin" }
            .self
            .createAfterHook {
                log(Log.DEBUG, TAG, "plugin disconnected")
                ModifierManager.close()
                restoreSystemClassLoader()
            }
    }

    override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
        EzXposed.initOnSystemServerStarting(param)
    }

    override fun onHotReloading(param: XposedModuleInterface.HotReloadingParam): Boolean {
        return super.onHotReloading(param)
    }

    override fun onHotReloaded(param: XposedModuleInterface.HotReloadedParam) {
        super.onHotReloaded(param)
    }

    private fun setPluginClassLoader(cl: ClassLoader) {
        ClassLoaderProvider.classLoader = cl
        EzXReflection.init(cl)
    }

    private fun restoreSystemClassLoader() {
        val sys = ClassLoader.getSystemClassLoader()
        ClassLoaderProvider.classLoader = sys
        EzXReflection.init(sys)
    }
}