package top.maplex.incisiontest.api

import taboolib.module.incision.api.VersionMatcher

/**
 * 测试用伪版本匹配器 —— 始终返回 "2.5"。
 *
 * 通过 `@Version(matcher = "top.maplex.incisiontest.api.FakeVersionMatcher")`
 * 接入 SurgeonScanner 的 `VersionMatchers.resolve` 通路（object INSTANCE 优先）。
 *
 * 用途：让区间过滤测试不依赖 NMS 环境就能稳定判定命中/不命中，
 * 覆盖"在区间内 / 早于 start / 晚于 end / 边界同点"四种情况。
 */
object FakeVersionMatcher : VersionMatcher {
    override fun current(): String = "2.5"
}
