package moe.imoli.hyperaod.ui

/**
 * 导航路由定义
 */
sealed class NavRoutes(val route: String) {
    /** 竖屏主界面 */
    data object Home : NavRoutes("home")

    /** 关于界面 */
    data object About : NavRoutes("about")

    /** 模块设置界面 */
    data object ModuleSettings : NavRoutes("module_settings")
}
