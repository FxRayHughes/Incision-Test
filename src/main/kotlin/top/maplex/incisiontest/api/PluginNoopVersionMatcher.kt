package top.maplex.incisiontest.api

import taboolib.module.incision.api.NoopVersionMatcher
import taboolib.module.incision.api.VersionMatcher

/**
 * 测试用 Noop 包装 —— FQCN 在插件命名空间，避免被 io.izzel.taboolib gradle 插件
 * 对 `taboolib.*` 字符串字面量的 shadow 重定位改写。
 *
 * 行为完全委托 [NoopVersionMatcher]：current=null，matches 永远返回 true。
 *
 * 直接在 `@Version(matcher = "taboolib.module.incision.api.NoopVersionMatcher")` 中
 * 使用 taboolib 前缀字面量时，shadow 会把字面量改写为
 * `top.maplex.incisiontest.taboolib.module.incision.api.NoopVersionMatcher`，
 * 导致运行时 ClassNotFound（incision 模块由 IsolatedClassLoader 加载，未被相对重定位）。
 *
 * 用本类即可绕过：
 *   `@Version(matcher = "top.maplex.incisiontest.api.PluginNoopVersionMatcher")`
 */
object PluginNoopVersionMatcher : VersionMatcher {
    override fun current(): String? = NoopVersionMatcher.current()
    override fun matches(start: String, end: String): Boolean = NoopVersionMatcher.matches(start, end)
}
