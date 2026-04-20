# Incision-Test

Incision 的 Bukkit/Paper 集成回归项目。这个 README 直接基于 `E:\Minecraft-Server\26.1.2\logs\latest.log` 的最后一轮全量运行结果生成，用来做速查、回归对照和耗时观察。

## 刷新本地依赖

```powershell
gradlew publishMavenPublicationToMavenLocalRepository -PdevLocal
```

## 基线

- 日志来源: `E:\Minecraft-Server\26.1.2\logs\latest.log`
- 基线时间: `2026-04-20 10:41:06`
- 测试结果: `340 pass / 0 fail / 340 total`
- 总耗时: `3535ms`
- 平均耗时: `10.4ms / case`
- 零耗时样本: `296` 个
- 最慢单例: `182ms`
- 备注: 日志中的 `VersionMatcher 解析失败` 是故意保留的 fallback 样本，分别覆盖不存在 matcher 的回退行为，不影响最终全绿。

## 分类总览

| 分类 | 数量 | 合计耗时 | 平均耗时 | 最高耗时 | 最低耗时 |
| --- | ---: | ---: | ---: | --- | --- |
| 基础 DSL | 31 | 2720ms | 87.74ms | `armon-disarmon` 182ms | `bad-descriptor` 0ms |
| Surgeon 注解 | 44 | 1ms | 0.02ms | `surgeon-where-dragon` 1ms | `surgeon-aop-after` 0ms |
| 元信息与 Kotlin | 54 | 388ms | 7.19ms | `surgerydesk-nested-runcase` 89ms | `kotlintarget-both-companion-path` 0ms |
| 平台与版本 | 45 | 184ms | 4.09ms | `bukkit-view-distance` 79ms | `cross-cl-bukkit-maxplayers` 0ms |
| 谓词与字节码 | 98 | 151ms | 1.54ms | `where-true` 76ms | `insn-pattern-accumulate` 0ms |
| 锚点矩阵与诊断 | 68 | 91ms | 1.34ms | `trauma-method-not-found` 48ms | `anchor-bypass-replace-result` 0ms |

## 最慢 10 条

| 用例 | 分类 | 耗时 | 摘要 |
| --- | --- | ---: | --- |
| `armon-disarmon` | 基础 DSL | 182ms | 验证 arm/disarm 生命周期切换后命中状态正确 |
| `priority-order` | 基础 DSL | 162ms | 验证多个 DSL advice 的优先级执行顺序 |
| `healall` | 基础 DSL | 161ms | 验证批量 healAll 会清空当前注册手术 |
| `exclusive` | 基础 DSL | 151ms | 验证互斥手术不会与其他手术重复生效 |
| `lead` | 基础 DSL | 106ms | 验证最基础的前置切入在方法入口触发一次 |
| `suspend-resume` | 基础 DSL | 101ms | 验证 suspend/resume 能在运行期切换手术启用状态 |
| `multi-target` | 基础 DSL | 100ms | 验证一条手术可以同时命中多个目标方法 |
| `primitive-int` | 基础 DSL | 94ms | 验证 `int` 返回值在织入后保持正确 |
| `heal` | 基础 DSL | 91ms | 验证单个 Suture heal 后立即失效 |
| `splice-proceed` | 基础 DSL | 91ms | 验证环绕 advice 放行原逻辑后仍能拿到正确返回值 |

## 基础 DSL

- 数量: `31`
- 合计耗时: `2720ms`
- 平均耗时: `87.74ms`
- 最高耗时: `armon-disarmon` `182ms`，验证 arm/disarm 生命周期切换后命中状态正确
- 最低耗时: `bad-descriptor` `0ms`，验证错误描述符输入会走诊断路径而不是静默成功

| 用例 | 耗时 | 亮点 | 摘要 | 优点 | 局限 |
| --- | ---: | --- | --- | --- | --- |
| `armon-disarmon` | 182ms | 启停状态切换 | 验证 arm/disarm 生命周期切换后命中状态正确 | 能覆盖显式启停接口的关键状态转换。 | 只覆盖线性切换，不覆盖并发切换。 |
| `priority-order` | 162ms | 基础排序基准 | 验证多个 DSL advice 的优先级执行顺序 | 能直接暴露排序与稳定顺序问题。 | 只覆盖当前注册集合，不代表跨类聚合顺序已完全覆盖。 |
| `healall` | 161ms | 全局回收入口 | 验证批量 healAll 会清空当前注册手术 | 能一次性覆盖全局回收入口。 | 如果存在跨作用域注册，这个用例不会替你分析来源。 |
| `exclusive` | 151ms | 互斥保护 | 验证互斥手术不会与其他手术重复生效 | 有助于发现重复命中和冲突注册。 | 只验证互斥结果，不解释冲突解决策略的全部细节。 |
| `lead` | 106ms | 入口切入最小样本 | 验证最基础的前置切入在方法入口触发一次 | 最适合做轻量前置探针，断言入口织入是否成立。 | 只能观察或改上下文，不能替代整段方法控制流。 |
| `suspend-resume` | 101ms | 运行时启停 | 验证 suspend/resume 能在运行期切换手术启用状态 | 适合确认启停不会丢失注册信息。 | 不证明多次嵌套 suspend 的计数语义。 |
| `multi-target` | 100ms | 多目标注册 | 验证一条手术可以同时命中多个目标方法 | 能确认 scope 扩展到多目标时仍保持稳定。 | 只适合语义相近的目标，不验证异构签名。 |
| `primitive-int` | 94ms | 原始类型桥接 | 验证 `int` 返回值在织入后保持正确 | 能覆盖最常见原始类型桥接。 | 只覆盖 `int`，不代表所有原始类型都已覆盖。 |
| `heal` | 91ms | 单体回收 | 验证单个 Suture heal 后立即失效 | 能快速确认卸载链路有效。 | 只验证单体回收，不验证批量回收。 |
| `splice-proceed` | 91ms | 环绕控制链验证 | 验证环绕 advice 放行原逻辑后仍能拿到正确返回值 | 可以直接覆盖最常见的 around + proceed 场景。 | 只证明放行链路正确，不代表短路链路也正确。 |
| `trail` | 90ms | 正常出口基准 | 验证尾部切入在正常返回路径触发一次 | 可以稳定覆盖方法退出点，适合确认收尾 advice 是否注册成功。 | 默认关注出口语义，不适合表达中段字节码锚点。 |
| `trail-throw` | 87ms | 异常出口覆盖 | 验证 Trail 在异常出口也能被触发 | 能同时覆盖 return 与 throw，两条出口语义清晰。 | 只验证出口时机，不负责中间指令级匹配。 |
| `auto-close` | 86ms | 资源关闭回收 | 验证 AutoCloseable/作用域关闭时手术自动回收 | 适合确认 try-with-resources 风格的生命周期管理。 | 只覆盖正常关闭路径，不覆盖资源泄漏排查。 |
| `void-target` | 86ms | 无返回值目标 | 验证无返回值方法在织入后仍可正常执行 | 能覆盖 `void`/`Unit` 目标的桥接与收尾逻辑。 | 不验证返回值改写类能力。 |
| `duplicate-id` | 81ms | 冲突注册诊断 | 验证重复 operation id 会被系统识别并处理 | 能提前暴露命名冲突导致的覆盖问题。 | 只关注重复 id，不覆盖 scope 冲突等其他冲突类型。 |
| `splice-skip` | 80ms | 环绕控制链验证 | 验证环绕 advice 可以短路原方法并直接返回 | 能快速确认 override/short-circuit 是否生效。 | 不覆盖 proceed 后继续修改结果的复合路径。 |
| `on-batch` | 79ms | 批量挂载路径 | 验证一组目标批量注册后都能生效 | 适合检查批量挂载路径与统一 heal 行为。 | 重心在批量注册，不细分单个目标的边界条件。 |
| `static-target` | 78ms | 静态目标桥接 | 验证静态方法目标也能被正常织入 | 覆盖非实例目标，能及时发现 self 为空的分派问题。 | 只验证静态方法，不涵盖 companion/@JvmStatic 变体。 |
| `wildcard` | 78ms | 通配描述符覆盖 | 验证 scope 通配参数写法能够命中目标方法 | 适合检验 descriptor 通配语法是否按预期生效。 | 只证明匹配范围，不证明重载分辨的精确性。 |
| `excise` | 76ms | 整段方法覆写 | 验证整段方法体被 Excise 完全替换 | 适合确认 overwrite 语义和原方法体彻底不执行。 | 一个目标只能存在一个 Excise，冲突成本最高。 |
| `transient-use` | 75ms | 短作用域手术 | 验证 transient 手术仅在作用域内短暂生效 | 能快速发现作用域泄漏问题。 | 只证明一次性使用场景，不代表长期 Suture 生命周期无误。 |
| `bypass` | 74ms | 单点调用替换 | 验证对单条调用指令做 redirect 替换 | 能准确证明 Bypass 只替换目标调用而不重写整段方法。 | 只适合可识别的单点调用，不适合整段控制流替换。 |
| `find-list` | 74ms | 可观测性接口 | 验证已注册手术可以被查询与列出 | 适合做运维与诊断能力回归。 | 只检查可见性，不校验查询结果的完整元数据。 |
| `threadlocal-active` | 74ms | 上下文隔离 | 验证启用状态会正确进入 ThreadLocal 上下文 | 能发现运行时上下文透传是否缺失。 | 只覆盖激活路径，不说明线程切换后的跨线程语义。 |
| `multi-arg` | 73ms | 多参数装配 | 验证多参数目标在织入后按原顺序传递 | 能同时观察多参数装配、顺序和类型桥接。 | 只覆盖固定参数列表，不验证 vararg。 |
| `scoped` | 73ms | 选择器约束 | 验证带 scope 约束的 DSL 手术只作用于指定目标 | 非常适合确认选择器没有误伤其他方法。 | scope 表达式仍受当前 DSL 能力边界限制。 |
| `splice-modify-args` | 73ms | 环绕控制链验证 | 验证环绕 advice 放行前改写入参 | 同时覆盖参数改写与 proceed(args...) 两段核心能力。 | 焦点在参数通道，不校验返回值二次改写。 |
| `primitive-bool` | 72ms | 原始类型桥接 | 验证 `boolean` 返回值在织入后保持正确 | 适合发现布尔值装箱与拆箱路径问题。 | 不覆盖枚举、对象或数组返回值。 |
| `primitive-long` | 72ms | 原始类型桥接 | 验证 `long` 返回值在织入后保持正确 | 能覆盖双槽原始类型返回值的桥接风险。 | 只验证 `long` 单场景，不覆盖更复杂的宽类型组合。 |
| `bad-descriptor` | 0ms | 非法描述符诊断 | 验证错误描述符输入会走诊断路径而不是静默成功 | 能保证非法声明尽早失败，便于定位。 | 只说明解析失败路径，不说明所有错误都具备同级诊断质量。 |
| `threadlocal-inactive` | 0ms | 上下文隔离 | 验证未启用时不会残留 ThreadLocal 上下文 | 能及时发现作用域清理失败。 | 不覆盖复杂嵌套切入造成的上下文复用问题。 |

## Surgeon 注解

- 数量: `44`
- 合计耗时: `1ms`
- 平均耗时: `0.02ms`
- 最高耗时: `surgeon-where-dragon` `1ms`，验证注解式 where 谓词 `dragon` 的筛选行为
- 最低耗时: `surgeon-aop-after` `0ms`，验证注解式 AOP 场景 `after` 的织入行为

| 用例 | 耗时 | 亮点 | 摘要 | 优点 | 局限 |
| --- | ---: | --- | --- | --- | --- |
| `surgeon-where-dragon` | 1ms | 注解式谓词筛选 | 验证注解式 where 谓词 `dragon` 的筛选行为 | 能证明注解 where 与运行时谓词引擎真正串起来了。 | where 只在匹配事件上求值，不替代 scope/descriptor 本身。 |
| `surgeon-aop-after` | 0ms | 注解式 AOP 回归 | 验证注解式 AOP 场景 `after` 的织入行为 | 贴近使用者真实写法，最适合回归注解扫描、注册和调用链。 | 主要覆盖 AOP 风格接口，不等同于低层 Site/InsnPattern 全覆盖。 |
| `surgeon-aop-around` | 0ms | 注解式 AOP 回归 | 验证注解式 AOP 场景 `around` 的织入行为 | 贴近使用者真实写法，最适合回归注解扫描、注册和调用链。 | 主要覆盖 AOP 风格接口，不等同于低层 Site/InsnPattern 全覆盖。 |
| `surgeon-aop-before` | 0ms | 注解式 AOP 回归 | 验证注解式 AOP 场景 `before` 的织入行为 | 贴近使用者真实写法，最适合回归注解扫描、注册和调用链。 | 主要覆盖 AOP 风格接口，不等同于低层 Site/InsnPattern 全覆盖。 |
| `surgeon-aop-companion-target` | 0ms | 注解式 AOP 回归 | 验证注解式 AOP 场景 `companion-target` 的织入行为 | 贴近使用者真实写法，最适合回归注解扫描、注册和调用链。 | 主要覆盖 AOP 风格接口，不等同于低层 Site/InsnPattern 全覆盖。 |
| `surgeon-aop-modify-args` | 0ms | 注解式 AOP 回归 | 验证注解式 AOP 场景 `modify-args` 的织入行为 | 贴近使用者真实写法，最适合回归注解扫描、注册和调用链。 | 主要覆盖 AOP 风格接口，不等同于低层 Site/InsnPattern 全覆盖。 |
| `surgeon-aop-proceed-result` | 0ms | 注解式 AOP 回归 | 验证注解式 AOP 场景 `proceed-result` 的织入行为 | 贴近使用者真实写法，最适合回归注解扫描、注册和调用链。 | 主要覆盖 AOP 风格接口，不等同于低层 Site/InsnPattern 全覆盖。 |
| `surgeon-aop-static-target` | 0ms | 注解式 AOP 回归 | 验证注解式 AOP 场景 `static-target` 的织入行为 | 贴近使用者真实写法，最适合回归注解扫描、注册和调用链。 | 主要覆盖 AOP 风格接口，不等同于低层 Site/InsnPattern 全覆盖。 |
| `surgeon-bypass` | 0ms | 注解扫描总链路 | 验证注解式 Surgeon 基础场景 `bypass` 的织入行为 | 覆盖最核心的注解扫描到注册全链路。 | 更强调入口正确性，不等同于字节码矩阵覆盖。 |
| `surgeon-bypass-invoke` | 0ms | 注解式 redirect | 验证注解式 Bypass 场景 `invoke` 的替换行为 | 能快速回归 redirect 风格的中段替换。 | 仅覆盖命中的调用点，不验证整段方法覆盖。 |
| `surgeon-default-priority` | 0ms | 注解扫描总链路 | 验证注解式 Surgeon 基础场景 `default-priority` 的织入行为 | 覆盖最核心的注解扫描到注册全链路。 | 更强调入口正确性，不等同于字节码矩阵覆盖。 |
| `surgeon-excise` | 0ms | 注解扫描总链路 | 验证注解式 Surgeon 基础场景 `excise` 的织入行为 | 覆盖最核心的注解扫描到注册全链路。 | 更强调入口正确性，不等同于字节码矩阵覆盖。 |
| `surgeon-graft-field-get` | 0ms | 注解式锚点植入 | 验证注解式 Graft 场景 `field-get` 的植入行为 | 适合检查 INVOKE/FIELD/NEW 等中段锚点的注解写法。 | 焦点在注解入口，不展开验证更复杂的 offset 组合。 |
| `surgeon-graft-field-put` | 0ms | 注解式锚点植入 | 验证注解式 Graft 场景 `field-put` 的植入行为 | 适合检查 INVOKE/FIELD/NEW 等中段锚点的注解写法。 | 焦点在注解入口，不展开验证更复杂的 offset 组合。 |
| `surgeon-graft-invoke-after` | 0ms | 注解式锚点植入 | 验证注解式 Graft 场景 `invoke-after` 的植入行为 | 适合检查 INVOKE/FIELD/NEW 等中段锚点的注解写法。 | 焦点在注解入口，不展开验证更复杂的 offset 组合。 |
| `surgeon-graft-invoke-before` | 0ms | 注解式锚点植入 | 验证注解式 Graft 场景 `invoke-before` 的植入行为 | 适合检查 INVOKE/FIELD/NEW 等中段锚点的注解写法。 | 焦点在注解入口，不展开验证更复杂的 offset 组合。 |
| `surgeon-graft-new` | 0ms | 注解式锚点植入 | 验证注解式 Graft 场景 `new` 的植入行为 | 适合检查 INVOKE/FIELD/NEW 等中段锚点的注解写法。 | 焦点在注解入口，不展开验证更复杂的 offset 组合。 |
| `surgeon-graft-throw` | 0ms | 注解式锚点植入 | 验证注解式 Graft 场景 `throw` 的植入行为 | 适合检查 INVOKE/FIELD/NEW 等中段锚点的注解写法。 | 焦点在注解入口，不展开验证更复杂的 offset 组合。 |
| `surgeon-insn-pattern` | 0ms | 注解扫描总链路 | 验证注解式 Surgeon 基础场景 `insn-pattern` 的织入行为 | 覆盖最核心的注解扫描到注册全链路。 | 更强调入口正确性，不等同于字节码矩阵覆盖。 |
| `surgeon-lead` | 0ms | 注解扫描总链路 | 验证注解式 Surgeon 基础场景 `lead` 的织入行为 | 覆盖最核心的注解扫描到注册全链路。 | 更强调入口正确性，不等同于字节码矩阵覆盖。 |
| `surgeon-mixin-overwrite` | 0ms | Mixin 语义回归 | 验证注解式 Mixin 场景 `overwrite` 的替换行为 | 能直接对应 overwrite/redirect 语义，问题定位直观。 | 只覆盖被测试的 mixin 语义，不代表全部 anchor 组合都正确。 |
| `surgeon-mixin-redirect` | 0ms | Mixin 语义回归 | 验证注解式 Mixin 场景 `redirect` 的替换行为 | 能直接对应 overwrite/redirect 语义，问题定位直观。 | 只覆盖被测试的 mixin 语义，不代表全部 anchor 组合都正确。 |
| `surgeon-multi-surgeon-priority` | 0ms | 注解扫描总链路 | 验证注解式 Surgeon 基础场景 `multi-surgeon-priority` 的织入行为 | 覆盖最核心的注解扫描到注册全链路。 | 更强调入口正确性，不等同于字节码矩阵覆盖。 |
| `surgeon-offset-ordinal` | 0ms | 注解式偏移定位 | 验证注解式 offset 场景 `ordinal` 的定位行为 | 能确认 annotation -> SiteSpec 的偏移转换正确。 | 只覆盖当前 fixture 的字节码形态。 |
| `surgeon-offset-shift` | 0ms | 注解式偏移定位 | 验证注解式 offset 场景 `shift` 的定位行为 | 能确认 annotation -> SiteSpec 的偏移转换正确。 | 只覆盖当前 fixture 的字节码形态。 |
| `surgeon-operation-custom-id` | 0ms | Operation 元信息 | 验证注解式 @Operation 场景 `custom-id` 的注册行为 | 能同时覆盖 id、enabled、priority 等元信息解析。 | 只验证单类注解声明，不替代全局调度顺序测试。 |
| `surgeon-operation-disabled` | 0ms | Operation 元信息 | 验证注解式 @Operation 场景 `disabled` 的注册行为 | 能同时覆盖 id、enabled、priority 等元信息解析。 | 只验证单类注解声明，不替代全局调度顺序测试。 |
| `surgeon-operation-disabled-resume` | 0ms | Operation 元信息 | 验证注解式 @Operation 场景 `disabled-resume` 的注册行为 | 能同时覆盖 id、enabled、priority 等元信息解析。 | 只验证单类注解声明，不替代全局调度顺序测试。 |
| `surgeon-operation-overrides-surgeon` | 0ms | Operation 元信息 | 验证注解式 @Operation 场景 `overrides-surgeon` 的注册行为 | 能同时覆盖 id、enabled、priority 等元信息解析。 | 只验证单类注解声明，不替代全局调度顺序测试。 |
| `surgeon-operation-priority` | 0ms | Operation 元信息 | 验证注解式 @Operation 场景 `priority` 的注册行为 | 能同时覆盖 id、enabled、priority 等元信息解析。 | 只验证单类注解声明，不替代全局调度顺序测试。 |
| `surgeon-operation-priority-low` | 0ms | Operation 元信息 | 验证注解式 @Operation 场景 `priority-low` 的注册行为 | 能同时覆盖 id、enabled、priority 等元信息解析。 | 只验证单类注解声明，不替代全局调度顺序测试。 |
| `surgeon-priority-default` | 0ms | 注解扫描总链路 | 验证注解式 @Surgeon 优先级场景 `default` | 适合发现类级默认优先级的排序与覆盖问题。 | 只验证注解默认值与排序，不覆盖运行时 suspend/resume。 |
| `surgeon-priority-extreme-max` | 0ms | 注解扫描总链路 | 验证注解式 @Surgeon 优先级场景 `extreme-max` | 适合发现类级默认优先级的排序与覆盖问题。 | 只验证注解默认值与排序，不覆盖运行时 suspend/resume。 |
| `surgeon-priority-extreme-min` | 0ms | 注解扫描总链路 | 验证注解式 @Surgeon 优先级场景 `extreme-min` | 适合发现类级默认优先级的排序与覆盖问题。 | 只验证注解默认值与排序，不覆盖运行时 suspend/resume。 |
| `surgeon-priority-negative` | 0ms | 注解扫描总链路 | 验证注解式 @Surgeon 优先级场景 `negative` | 适合发现类级默认优先级的排序与覆盖问题。 | 只验证注解默认值与排序，不覆盖运行时 suspend/resume。 |
| `surgeon-priority-order-all` | 0ms | 注解扫描总链路 | 验证注解式 @Surgeon 优先级场景 `order-all` | 适合发现类级默认优先级的排序与覆盖问题。 | 只验证注解默认值与排序，不覆盖运行时 suspend/resume。 |
| `surgeon-priority-positive` | 0ms | 注解扫描总链路 | 验证注解式 @Surgeon 优先级场景 `positive` | 适合发现类级默认优先级的排序与覆盖问题。 | 只验证注解默认值与排序，不覆盖运行时 suspend/resume。 |
| `surgeon-priority-zero-explicit` | 0ms | 注解扫描总链路 | 验证注解式 @Surgeon 优先级场景 `zero-explicit` | 适合发现类级默认优先级的排序与覆盖问题。 | 只验证注解默认值与排序，不覆盖运行时 suspend/resume。 |
| `surgeon-splice` | 0ms | 注解扫描总链路 | 验证注解式 Surgeon 基础场景 `splice` 的织入行为 | 覆盖最核心的注解扫描到注册全链路。 | 更强调入口正确性，不等同于字节码矩阵覆盖。 |
| `surgeon-three-object-order` | 0ms | 注解扫描总链路 | 验证注解式 Surgeon 基础场景 `three-object-order` 的织入行为 | 覆盖最核心的注解扫描到注册全链路。 | 更强调入口正确性，不等同于字节码矩阵覆盖。 |
| `surgeon-trail` | 0ms | 注解扫描总链路 | 验证注解式 Surgeon 基础场景 `trail` 的织入行为 | 覆盖最核心的注解扫描到注册全链路。 | 更强调入口正确性，不等同于字节码矩阵覆盖。 |
| `surgeon-trim-arg` | 0ms | 注解式值改写 | 验证注解式 Trim 场景 `arg` 的改写行为 | 适合断言参数或返回值在注解模式下被改写。 | 不覆盖局部变量全部槽位与全部类型组合。 |
| `surgeon-trim-return` | 0ms | 注解式值改写 | 验证注解式 Trim 场景 `return` 的改写行为 | 适合断言参数或返回值在注解模式下被改写。 | 不覆盖局部变量全部槽位与全部类型组合。 |
| `surgeon-where-low-level` | 0ms | 注解式谓词筛选 | 验证注解式 where 谓词 `low-level` 的筛选行为 | 能证明注解 where 与运行时谓词引擎真正串起来了。 | where 只在匹配事件上求值，不替代 scope/descriptor 本身。 |

## 元信息与 Kotlin

- 数量: `54`
- 合计耗时: `388ms`
- 平均耗时: `7.19ms`
- 最高耗时: `surgerydesk-nested-runcase` `89ms`，验证 @SurgeryDesk 场景 `nested-runcase` 的调用约束
- 最低耗时: `kotlintarget-both-companion-path` `0ms`，验证 @KotlinTarget 场景 `both-companion-path` 的目标扩展行为

| 用例 | 耗时 | 亮点 | 摘要 | 优点 | 局限 |
| --- | ---: | --- | --- | --- | --- |
| `surgerydesk-nested-runcase` | 89ms | DSL 调用方校验 | 验证 @SurgeryDesk 场景 `nested-runcase` 的调用约束 | 能保证 transient DSL 的调用方约束被真正执行。 | 只聚焦调用入口，不涵盖所有 DSL 操作。 |
| `surgerydesk-direct-call` | 80ms | DSL 调用方校验 | 验证 @SurgeryDesk 场景 `direct-call` 的调用约束 | 能保证 transient DSL 的调用方约束被真正执行。 | 只聚焦调用入口，不涵盖所有 DSL 操作。 |
| `desk-allcases-self` | 77ms | 多工作台并存 | 验证不同 SurgeryDesk 调用者 `allcases-self` 的优先级与作用域 | 适合回归多个 desk object 并存时的行为。 | 只覆盖当前 desk fixture，不等于跨模块调用都安全。 |
| `desk-default-transient` | 52ms | 多工作台并存 | 验证不同 SurgeryDesk 调用者 `default-transient` 的优先级与作用域 | 适合回归多个 desk object 并存时的行为。 | 只覆盖当前 desk fixture，不等于跨模块调用都安全。 |
| `desk-high-priority-transient` | 51ms | 多工作台并存 | 验证不同 SurgeryDesk 调用者 `high-priority-transient` 的优先级与作用域 | 适合回归多个 desk object 并存时的行为。 | 只覆盖当前 desk fixture，不等于跨模块调用都安全。 |
| `desk-negative-priority-transient` | 39ms | 多工作台并存 | 验证不同 SurgeryDesk 调用者 `negative-priority-transient` 的优先级与作用域 | 适合回归多个 desk object 并存时的行为。 | 只覆盖当前 desk fixture，不等于跨模块调用都安全。 |
| `kotlintarget-both-companion-path` | 0ms | Kotlin companion/@JvmStatic | 验证 @KotlinTarget 场景 `both-companion-path` 的目标扩展行为 | 能区分 companion 实例方法与 @JvmStatic 桥接方法。 | 依赖 Kotlin 生成代码形态，跨编译器版本时需警惕差异。 |
| `kotlintarget-both-static-path` | 0ms | Kotlin companion/@JvmStatic | 验证 @KotlinTarget 场景 `both-static-path` 的目标扩展行为 | 能区分 companion 实例方法与 @JvmStatic 桥接方法。 | 依赖 Kotlin 生成代码形态，跨编译器版本时需警惕差异。 |
| `kotlintarget-companion-only` | 0ms | Kotlin companion/@JvmStatic | 验证 @KotlinTarget 场景 `companion-only` 的目标扩展行为 | 能区分 companion 实例方法与 @JvmStatic 桥接方法。 | 依赖 Kotlin 生成代码形态，跨编译器版本时需警惕差异。 |
| `kotlintarget-jvmstatic-only` | 0ms | Kotlin companion/@JvmStatic | 验证 @KotlinTarget 场景 `jvmstatic-only` 的目标扩展行为 | 能区分 companion 实例方法与 @JvmStatic 桥接方法。 | 依赖 Kotlin 生成代码形态，跨编译器版本时需警惕差异。 |
| `ktarget-ff-alt` | 0ms | KotlinTarget 四象限 | 验证 KotlinTarget 矩阵场景 `ff-alt` 的覆盖结果 | 适合对四象限组合做回归，不容易漏格。 | 主要验证目标扩展矩阵，不验证 advice 自身逻辑复杂度。 |
| `ktarget-ft-alt` | 0ms | KotlinTarget 四象限 | 验证 KotlinTarget 矩阵场景 `ft-alt` 的覆盖结果 | 适合对四象限组合做回归，不容易漏格。 | 主要验证目标扩展矩阵，不验证 advice 自身逻辑复杂度。 |
| `ktarget-matrix-ff` | 0ms | KotlinTarget 四象限 | 验证 KotlinTarget 矩阵场景 `matrix-ff` 的覆盖结果 | 适合对四象限组合做回归，不容易漏格。 | 主要验证目标扩展矩阵，不验证 advice 自身逻辑复杂度。 |
| `ktarget-matrix-ft` | 0ms | KotlinTarget 四象限 | 验证 KotlinTarget 矩阵场景 `matrix-ft` 的覆盖结果 | 适合对四象限组合做回归，不容易漏格。 | 主要验证目标扩展矩阵，不验证 advice 自身逻辑复杂度。 |
| `ktarget-matrix-tf` | 0ms | KotlinTarget 四象限 | 验证 KotlinTarget 矩阵场景 `matrix-tf` 的覆盖结果 | 适合对四象限组合做回归，不容易漏格。 | 主要验证目标扩展矩阵，不验证 advice 自身逻辑复杂度。 |
| `ktarget-matrix-tt-companion` | 0ms | KotlinTarget 四象限 | 验证 KotlinTarget 矩阵场景 `matrix-tt-companion` 的覆盖结果 | 适合对四象限组合做回归，不容易漏格。 | 主要验证目标扩展矩阵，不验证 advice 自身逻辑复杂度。 |
| `ktarget-matrix-tt-static` | 0ms | KotlinTarget 四象限 | 验证 KotlinTarget 矩阵场景 `matrix-tt-static` 的覆盖结果 | 适合对四象限组合做回归，不容易漏格。 | 主要验证目标扩展矩阵，不验证 advice 自身逻辑复杂度。 |
| `ktarget-tf-alt` | 0ms | KotlinTarget 四象限 | 验证 KotlinTarget 矩阵场景 `tf-alt` 的覆盖结果 | 适合对四象限组合做回归，不容易漏格。 | 主要验证目标扩展矩阵，不验证 advice 自身逻辑复杂度。 |
| `ktarget-tt-alt-companion` | 0ms | KotlinTarget 四象限 | 验证 KotlinTarget 矩阵场景 `tt-alt-companion` 的覆盖结果 | 适合对四象限组合做回归，不容易漏格。 | 主要验证目标扩展矩阵，不验证 advice 自身逻辑复杂度。 |
| `ktarget-tt-alt-static` | 0ms | KotlinTarget 四象限 | 验证 KotlinTarget 矩阵场景 `tt-alt-static` 的覆盖结果 | 适合对四象限组合做回归，不容易漏格。 | 主要验证目标扩展矩阵，不验证 advice 自身逻辑复杂度。 |
| `op-disable-resume-suspend-cycle` | 0ms | Operation 生命周期 | 验证 Operation 相关场景 `disable-resume-suspend-cycle` 的运行行为 | 适合收敛 operation 元信息的公共回归。 | 描述偏概括，遇到特殊案例仍应补充精确文档。 |
| `op-enpri-false-0` | 0ms | 启用开关 × 优先级 | 验证 @Operation enabled/priority 场景 `false-0` | 能快速看出启用开关与优先级叠加是否正确。 | 只验证二维矩阵，不覆盖自定义 id。 |
| `op-enpri-false-100` | 0ms | 启用开关 × 优先级 | 验证 @Operation enabled/priority 场景 `false-100` | 能快速看出启用开关与优先级叠加是否正确。 | 只验证二维矩阵，不覆盖自定义 id。 |
| `op-enpri-false-5` | 0ms | 启用开关 × 优先级 | 验证 @Operation enabled/priority 场景 `false-5` | 能快速看出启用开关与优先级叠加是否正确。 | 只验证二维矩阵，不覆盖自定义 id。 |
| `op-enpri-false-neg10` | 0ms | 启用开关 × 优先级 | 验证 @Operation enabled/priority 场景 `false-neg10` | 能快速看出启用开关与优先级叠加是否正确。 | 只验证二维矩阵，不覆盖自定义 id。 |
| `op-enpri-true-0` | 0ms | 启用开关 × 优先级 | 验证 @Operation enabled/priority 场景 `true-0` | 能快速看出启用开关与优先级叠加是否正确。 | 只验证二维矩阵，不覆盖自定义 id。 |
| `op-enpri-true-100` | 0ms | 启用开关 × 优先级 | 验证 @Operation enabled/priority 场景 `true-100` | 能快速看出启用开关与优先级叠加是否正确。 | 只验证二维矩阵，不覆盖自定义 id。 |
| `op-enpri-true-5` | 0ms | 启用开关 × 优先级 | 验证 @Operation enabled/priority 场景 `true-5` | 能快速看出启用开关与优先级叠加是否正确。 | 只验证二维矩阵，不覆盖自定义 id。 |
| `op-enpri-true-neg10` | 0ms | 启用开关 × 优先级 | 验证 @Operation enabled/priority 场景 `true-neg10` | 能快速看出启用开关与优先级叠加是否正确。 | 只验证二维矩阵，不覆盖自定义 id。 |
| `op-enum-coverage` | 0ms | Operation 生命周期 | 验证 Operation 相关场景 `enum-coverage` 的运行行为 | 适合收敛 operation 元信息的公共回归。 | 描述偏概括，遇到特殊案例仍应补充精确文档。 |
| `op-id-empty-fallback` | 0ms | Operation ID 规则 | 验证 @Operation id 场景 `empty-fallback` 的命名行为 | 专门盯住 operation id 的解析、保留与回退规则。 | 只关注命名，不关注执行顺序。 |
| `op-id-normal` | 0ms | Operation ID 规则 | 验证 @Operation id 场景 `normal` 的命名行为 | 专门盯住 operation id 的解析、保留与回退规则。 | 只关注命名，不关注执行顺序。 |
| `op-id-special-chars` | 0ms | Operation ID 规则 | 验证 @Operation id 场景 `special-chars` 的命名行为 | 专门盯住 operation id 的解析、保留与回退规则。 | 只关注命名，不关注执行顺序。 |
| `op-id-very-long` | 0ms | Operation ID 规则 | 验证 @Operation id 场景 `very-long` 的命名行为 | 专门盯住 operation id 的解析、保留与回退规则。 | 只关注命名，不关注执行顺序。 |
| `op-matrix-m1-default` | 0ms | Operation 三维矩阵 | 验证 @Operation 三维矩阵场景 `m1-default` | 能系统性回归 id、enabled、priority 的组合关系。 | 矩阵覆盖面广，但每个格子本身都保持轻量，不深入业务逻辑。 |
| `op-matrix-m2-id-only` | 0ms | Operation 三维矩阵 | 验证 @Operation 三维矩阵场景 `m2-id-only` | 能系统性回归 id、enabled、priority 的组合关系。 | 矩阵覆盖面广，但每个格子本身都保持轻量，不深入业务逻辑。 |
| `op-matrix-m3-disabled` | 0ms | Operation 三维矩阵 | 验证 @Operation 三维矩阵场景 `m3-disabled` | 能系统性回归 id、enabled、priority 的组合关系。 | 矩阵覆盖面广，但每个格子本身都保持轻量，不深入业务逻辑。 |
| `op-matrix-m4-priority-pos` | 0ms | Operation 三维矩阵 | 验证 @Operation 三维矩阵场景 `m4-priority-pos` | 能系统性回归 id、enabled、priority 的组合关系。 | 矩阵覆盖面广，但每个格子本身都保持轻量，不深入业务逻辑。 |
| `op-matrix-m5-priority-neg` | 0ms | Operation 三维矩阵 | 验证 @Operation 三维矩阵场景 `m5-priority-neg` | 能系统性回归 id、enabled、priority 的组合关系。 | 矩阵覆盖面广，但每个格子本身都保持轻量，不深入业务逻辑。 |
| `op-matrix-m6-id-priority` | 0ms | Operation 三维矩阵 | 验证 @Operation 三维矩阵场景 `m6-id-priority` | 能系统性回归 id、enabled、priority 的组合关系。 | 矩阵覆盖面广，但每个格子本身都保持轻量，不深入业务逻辑。 |
| `op-matrix-m7-id-disabled` | 0ms | Operation 三维矩阵 | 验证 @Operation 三维矩阵场景 `m7-id-disabled` | 能系统性回归 id、enabled、priority 的组合关系。 | 矩阵覆盖面广，但每个格子本身都保持轻量，不深入业务逻辑。 |
| `op-matrix-m8-all-three` | 0ms | Operation 三维矩阵 | 验证 @Operation 三维矩阵场景 `m8-all-three` | 能系统性回归 id、enabled、priority 的组合关系。 | 矩阵覆盖面广，但每个格子本身都保持轻量，不深入业务逻辑。 |
| `op-matrix-m9-explicit-zero` | 0ms | Operation 三维矩阵 | 验证 @Operation 三维矩阵场景 `m9-explicit-zero` | 能系统性回归 id、enabled、priority 的组合关系。 | 矩阵覆盖面广，但每个格子本身都保持轻量，不深入业务逻辑。 |
| `op-matrix-priority-order` | 0ms | Operation 三维矩阵 | 验证 @Operation 三维矩阵场景 `priority-order` | 能系统性回归 id、enabled、priority 的组合关系。 | 矩阵覆盖面广，但每个格子本身都保持轻量，不深入业务逻辑。 |
| `op-same-priority-stable-order` | 0ms | Operation 生命周期 | 验证 Operation 相关场景 `same-priority-stable-order` 的运行行为 | 适合收敛 operation 元信息的公共回归。 | 描述偏概括，遇到特殊案例仍应补充精确文档。 |
| `priority-operation-default` | 0ms | 排序矩阵 | 验证优先级场景 `operation-default` 的执行顺序 | 直接观察排序结果，能及时暴露稳定序问题。 | 排序正确不等于 advice 语义本身正确。 |
| `priority-operation-neg10` | 0ms | 排序矩阵 | 验证优先级场景 `operation-neg10` 的执行顺序 | 直接观察排序结果，能及时暴露稳定序问题。 | 排序正确不等于 advice 语义本身正确。 |
| `priority-operation-order` | 0ms | 排序矩阵 | 验证优先级场景 `operation-order` 的执行顺序 | 直接观察排序结果，能及时暴露稳定序问题。 | 排序正确不等于 advice 语义本身正确。 |
| `priority-operation-pos200` | 0ms | 排序矩阵 | 验证优先级场景 `operation-pos200` 的执行顺序 | 直接观察排序结果，能及时暴露稳定序问题。 | 排序正确不等于 advice 语义本身正确。 |
| `priority-operation-pos50` | 0ms | 排序矩阵 | 验证优先级场景 `operation-pos50` 的执行顺序 | 直接观察排序结果，能及时暴露稳定序问题。 | 排序正确不等于 advice 语义本身正确。 |
| `priority-surgeon-default` | 0ms | 排序矩阵 | 验证优先级场景 `surgeon-default` 的执行顺序 | 直接观察排序结果，能及时暴露稳定序问题。 | 排序正确不等于 advice 语义本身正确。 |
| `priority-surgeon-high` | 0ms | 排序矩阵 | 验证优先级场景 `surgeon-high` 的执行顺序 | 直接观察排序结果，能及时暴露稳定序问题。 | 排序正确不等于 advice 语义本身正确。 |
| `priority-surgeon-low-negative` | 0ms | 排序矩阵 | 验证优先级场景 `surgeon-low-negative` 的执行顺序 | 直接观察排序结果，能及时暴露稳定序问题。 | 排序正确不等于 advice 语义本身正确。 |
| `priority-surgeon-multi-class-order` | 0ms | 排序矩阵 | 验证优先级场景 `surgeon-multi-class-order` 的执行顺序 | 直接观察排序结果，能及时暴露稳定序问题。 | 排序正确不等于 advice 语义本身正确。 |

## 平台与版本

- 数量: `45`
- 合计耗时: `184ms`
- 平均耗时: `4.09ms`
- 最高耗时: `bukkit-view-distance` `79ms`，验证 Bukkit API 场景 `view-distance` 的织入行为
- 最低耗时: `cross-cl-bukkit-maxplayers` `0ms`，验证跨 ClassLoader 场景 `bukkit-maxplayers` 的分派行为

| 用例 | 耗时 | 亮点 | 摘要 | 优点 | 局限 |
| --- | ---: | --- | --- | --- | --- |
| `bukkit-view-distance` | 79ms | Bukkit 平台实战 | 验证 Bukkit API 场景 `view-distance` 的织入行为 | 直接贴近真实 Bukkit 平台方法，价值高。 | 依赖 Bukkit 实际运行环境，脱离平台无法说明全部结果。 |
| `nms-motd` | 55ms | NMS / remap 实战 | 验证 NMS 场景 `motd` 的 remap/织入行为 | 能覆盖最容易出问题的服务端内部类路径。 | 高度依赖具体服务端版本与 remap 结果。 |
| `bukkit-max-players` | 43ms | Bukkit 平台实战 | 验证 Bukkit API 场景 `max-players` 的织入行为 | 直接贴近真实 Bukkit 平台方法，价值高。 | 依赖 Bukkit 实际运行环境，脱离平台无法说明全部结果。 |
| `cross-cl-nms-ishardcore` | 7ms | 跨 ClassLoader 分派 | 验证跨 ClassLoader 场景 `nms-ishardcore` 的分派行为 | 能提前发现 bridge、上下文类加载器和宿主切换问题。 | 强依赖测试运行环境，问题复现通常只在特定平台出现。 |
| `cross-cl-bukkit-maxplayers` | 0ms | 跨 ClassLoader 分派 | 验证跨 ClassLoader 场景 `bukkit-maxplayers` 的分派行为 | 能提前发现 bridge、上下文类加载器和宿主切换问题。 | 强依赖测试运行环境，问题复现通常只在特定平台出现。 |
| `cross-cl-nms-playercount` | 0ms | 跨 ClassLoader 分派 | 验证跨 ClassLoader 场景 `nms-playercount` 的分派行为 | 能提前发现 bridge、上下文类加载器和宿主切换问题。 | 强依赖测试运行环境，问题复现通常只在特定平台出现。 |
| `remap-field-resolve` | 0ms | 映射翻译 | 验证 remap 场景 `field-resolve` 的翻译行为 | 适合定位 owner/name/desc 的映射失配问题。 | 只说明映射结果，不验证最终织入后的运行行为。 |
| `remap-method-empty-desc` | 0ms | 映射翻译 | 验证 remap 场景 `method-empty-desc` 的翻译行为 | 适合定位 owner/name/desc 的映射失配问题。 | 只说明映射结果，不验证最终织入后的运行行为。 |
| `remap-method-fallback` | 0ms | 映射翻译 | 验证 remap 场景 `method-fallback` 的翻译行为 | 适合定位 owner/name/desc 的映射失配问题。 | 只说明映射结果，不验证最终织入后的运行行为。 |
| `remap-nms-method-name` | 0ms | 映射翻译 | 验证 remap 场景 `nms-method-name` 的翻译行为 | 适合定位 owner/name/desc 的映射失配问题。 | 只说明映射结果，不验证最终织入后的运行行为。 |
| `remap-owner-non-nms` | 0ms | 映射翻译 | 验证 remap 场景 `owner-non-nms` 的翻译行为 | 适合定位 owner/name/desc 的映射失配问题。 | 只说明映射结果，不验证最终织入后的运行行为。 |
| `remap-owner-translate` | 0ms | 映射翻译 | 验证 remap 场景 `owner-translate` 的翻译行为 | 适合定位 owner/name/desc 的映射失配问题。 | 只说明映射结果，不验证最终织入后的运行行为。 |
| `version-advice-bypass` | 0ms | 版本门控 | 验证版本匹配场景 `advice-bypass` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-advice-excise` | 0ms | 版本门控 | 验证版本匹配场景 `advice-excise` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-advice-graft` | 0ms | 版本门控 | 验证版本匹配场景 `advice-graft` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-advice-lead` | 0ms | 版本门控 | 验证版本匹配场景 `advice-lead` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-advice-splice` | 0ms | 版本门控 | 验证版本匹配场景 `advice-splice` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-advice-trail` | 0ms | 版本门控 | 验证版本匹配场景 `advice-trail` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-advice-trim` | 0ms | 版本门控 | 验证版本匹配场景 `advice-trim` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-fake-above-end` | 0ms | 版本门控 | 验证版本匹配场景 `fake-above-end` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-fake-below-start` | 0ms | 版本门控 | 验证版本匹配场景 `fake-below-start` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-fake-boundary-end` | 0ms | 版本门控 | 验证版本匹配场景 `fake-boundary-end` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-fake-boundary-start` | 0ms | 版本门控 | 验证版本匹配场景 `fake-boundary-start` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-fake-in-range` | 0ms | 版本门控 | 验证版本匹配场景 `fake-in-range` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-matcher-caching` | 0ms | 版本门控 | 验证版本匹配场景 `matcher-caching` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-matcher-default` | 0ms | 版本门控 | 验证版本匹配场景 `matcher-default` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-matcher-explicit-noop` | 0ms | 版本门控 | 验证版本匹配场景 `matcher-explicit-noop` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-matcher-fake-nobounds` | 0ms | 版本门控 | 验证版本匹配场景 `matcher-fake-nobounds` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-matcher-resolve` | 0ms | 版本门控 | 验证版本匹配场景 `matcher-resolve` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-matcher-segment` | 0ms | 版本门控 | 验证版本匹配场景 `matcher-segment` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-matrix-both-empty` | 0ms | 版本门控 | 验证版本匹配场景 `matrix-both-empty` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-matrix-inverted` | 0ms | 版本门控 | 验证版本匹配场景 `matrix-inverted` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-matrix-only-end` | 0ms | 版本门控 | 验证版本匹配场景 `matrix-only-end` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-matrix-only-start` | 0ms | 版本门控 | 验证版本匹配场景 `matrix-only-start` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-matrix-single-point` | 0ms | 版本门控 | 验证版本匹配场景 `matrix-single-point` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-mc-matcher-probe` | 0ms | 版本门控 | 验证版本匹配场景 `mc-matcher-probe` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-micro-above` | 0ms | 版本门控 | 验证版本匹配场景 `micro-above` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-micro-below` | 0ms | 版本门控 | 验证版本匹配场景 `micro-below` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-missing-matcher-fallback` | 0ms | 版本门控 | 验证版本匹配场景 `missing-matcher-fallback` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-multi-range-index` | 0ms | 版本门控 | 验证版本匹配场景 `multi-range-index` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-noop-matcher` | 0ms | 版本门控 | 验证版本匹配场景 `noop-matcher` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-null-current-matches` | 0ms | 版本门控 | 验证版本匹配场景 `null-current-matches` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-range-hit` | 0ms | 版本门控 | 验证版本匹配场景 `range-hit` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-segment-longer-end` | 0ms | 版本门控 | 验证版本匹配场景 `segment-longer-end` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |
| `version-segment-longer-start` | 0ms | 版本门控 | 验证版本匹配场景 `segment-longer-start` 的筛选行为 | 能确认 VersionMatcher、区间边界与缓存逻辑是否一致。 | 只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。 |

## 谓词与字节码

- 数量: `98`
- 合计耗时: `151ms`
- 平均耗时: `1.54ms`
- 最高耗时: `where-true` `76ms`，验证 where 谓词 `true` 的解析与求值行为
- 最低耗时: `insn-pattern-accumulate` `0ms`，验证 InsnPattern 场景 `accumulate` 的字节码匹配结果

| 用例 | 耗时 | 亮点 | 摘要 | 优点 | 局限 |
| --- | ---: | --- | --- | --- | --- |
| `where-true` | 76ms | 谓词 DSL 回归 | 验证 where 谓词 `true` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-false` | 75ms | 谓词 DSL 回归 | 验证 where 谓词 `false` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `insn-pattern-accumulate` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `accumulate` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-anewarray` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `anewarray` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-any` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `any` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-arraylength` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `arraylength` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-athrow` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `athrow` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-checkcast` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `checkcast` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-cst-numeric` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `cst-numeric` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-desc-exact` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `desc-exact` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-dup` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `dup` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-empty-steps` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `empty-steps` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-getfield` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `getfield` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-glob-desc` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `glob-desc` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-glob-name-suffix` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `glob-name-suffix` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-glob-owner-javalang` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `glob-owner-javalang` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-goto` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `goto` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-iconst5` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `iconst5` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-ifeq` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `ifeq` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-instanceof` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `instanceof` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-invokeinterface` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `invokeinterface` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-invokespecial` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `invokespecial` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-invokestatic` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `invokestatic` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-invokevirtual-glob` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `invokevirtual-glob` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-ireturn` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `ireturn` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-ldc` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `ldc` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-ldc-cst` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `ldc-cst` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-new` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `new` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-newarray` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `newarray` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-nop` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `nop` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-putfield` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `putfield` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-repeat` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `repeat` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-repeat2` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `repeat2` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-repeat5` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `repeat5` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-seq2` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `seq2` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-seq3` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `seq3` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-seq4` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `seq4` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `insn-pattern-seq5` | 0ms | 字节码序列匹配 | 验证 InsnPattern 场景 `seq5` 的字节码匹配结果 | 直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。 | 高度依赖编译后字节码形态，源码微调就可能改变期望。 |
| `offset-after-0` | 0ms | 相对偏移基准 | 验证 offset 场景 `after-0` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `offset-after-1-all` | 0ms | 相对偏移基准 | 验证 offset 场景 `after-1-all` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `offset-after-2-all` | 0ms | 相对偏移基准 | 验证 offset 场景 `after-2-all` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `offset-any-ordinal` | 0ms | 相对偏移基准 | 验证 offset 场景 `any-ordinal` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `offset-before-2-all` | 0ms | 相对偏移基准 | 验证 offset 场景 `before-2-all` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `offset-before-neg-1` | 0ms | 相对偏移基准 | 验证 offset 场景 `before-neg-1` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `offset-fieldget` | 0ms | 相对偏移基准 | 验证 offset 场景 `fieldget` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `offset-fieldput` | 0ms | 相对偏移基准 | 验证 offset 场景 `fieldput` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `offset-minus-2` | 0ms | 相对偏移基准 | 验证 offset 场景 `minus-2` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `offset-ordinal0-after-1` | 0ms | 相对偏移基准 | 验证 offset 场景 `ordinal0-after-1` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `offset-ordinal1-after-1` | 0ms | 相对偏移基准 | 验证 offset 场景 `ordinal1-after-1` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `offset-ordinal2-after-2` | 0ms | 相对偏移基准 | 验证 offset 场景 `ordinal2-after-2` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `offset-plus-5` | 0ms | 相对偏移基准 | 验证 offset 场景 `plus-5` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `offset-zero` | 0ms | 相对偏移基准 | 验证 offset 场景 `zero` 的相对偏移行为 | 能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。 | 依赖具体指令序列，换字节码后需要同步维护。 |
| `opcodeseq-head-iconst5` | 0ms | OpcodeSeq 落点 | 验证 OpcodeSeq 场景 `head-iconst5` 的落点行为 | 适合检查 head/invoke 等定位器与序列匹配协作是否正确。 | 只覆盖定义好的序列样本，不代表所有 opcode 组合。 |
| `opcodeseq-head-ifeq` | 0ms | OpcodeSeq 落点 | 验证 OpcodeSeq 场景 `head-ifeq` 的落点行为 | 适合检查 head/invoke 等定位器与序列匹配协作是否正确。 | 只覆盖定义好的序列样本，不代表所有 opcode 组合。 |
| `opcodeseq-invoke-after` | 0ms | OpcodeSeq 落点 | 验证 OpcodeSeq 场景 `invoke-after` 的落点行为 | 适合检查 head/invoke 等定位器与序列匹配协作是否正确。 | 只覆盖定义好的序列样本，不代表所有 opcode 组合。 |
| `opcodeseq-invoke-glob` | 0ms | OpcodeSeq 落点 | 验证 OpcodeSeq 场景 `invoke-glob` 的落点行为 | 适合检查 head/invoke 等定位器与序列匹配协作是否正确。 | 只覆盖定义好的序列样本，不代表所有 opcode 组合。 |
| `opcodeseq-invoke-pattern` | 0ms | OpcodeSeq 落点 | 验证 OpcodeSeq 场景 `invoke-pattern` 的落点行为 | 适合检查 head/invoke 等定位器与序列匹配协作是否正确。 | 只覆盖定义好的序列样本，不代表所有 opcode 组合。 |
| `where-and` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `and` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-as-cast` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `as-cast` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-compound` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `compound` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-empty-baseline` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `empty-baseline` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-eq-string` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `eq-string` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-ge` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `ge` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-gt` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `gt` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-ic-arraylist` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `ic-arraylist` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-in-list` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `in-list` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-in-string` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `in-string` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-ip-object` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `ip-object` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-is-string` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `is-string` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-it-exact` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `it-exact` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-le` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `le` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-literal-double` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `literal-double` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-literal-false` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `literal-false` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-literal-null` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `literal-null` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-literal-true` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `literal-true` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-lt` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `lt` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-matches` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `matches` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-method-call` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `method-call` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-method-noarg` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `method-noarg` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-neq-int` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `neq-int` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-nested` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `nested` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-not-group` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `not-group` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-not-is-int` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `not-is-int` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-not-it` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `not-it` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-on-bypass` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `on-bypass` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-on-excise` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `on-excise` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-on-graft` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `on-graft` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-on-splice` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `on-splice` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-on-trail` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `on-trail` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-on-trim` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `on-trim` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-or` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `or` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-property` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `property` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-property-size` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `property-size` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-safe-call` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `safe-call` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-this-ref` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `this-ref` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-trauma-method-indexed` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `trauma-method-indexed` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-trauma-syntax` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `trauma-syntax` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |
| `where-trauma-undef` | 0ms | 谓词 DSL 回归 | 验证 where 谓词 `trauma-undef` 的解析与求值行为 | 能把谓词 DSL 的语法与运行效果拆开单测，定位快。 | 重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。 |

## 锚点矩阵与诊断

- 数量: `68`
- 合计耗时: `91ms`
- 平均耗时: `1.34ms`
- 最高耗时: `trauma-method-not-found` `48ms`，验证诊断场景 `method-not-found` 会产生预期错误信息
- 最低耗时: `anchor-bypass-replace-result` `0ms`，验证锚点组合 `bypass-replace-result` 的织入行为

| 用例 | 耗时 | 亮点 | 摘要 | 优点 | 局限 |
| --- | ---: | --- | --- | --- | --- |
| `trauma-method-not-found` | 48ms | 错误诊断回归 | 验证诊断场景 `method-not-found` 会产生预期错误信息 | 确保坏声明不会静默吞掉，方便定位用户配置错误。 | 错误文案可能随实现细节调整，断言需要留余地。 |
| `trauma-descriptor-wrong-encoding` | 43ms | 错误诊断回归 | 验证诊断场景 `descriptor-wrong-encoding` 会产生预期错误信息 | 确保坏声明不会静默吞掉，方便定位用户配置错误。 | 错误文案可能随实现细节调整，断言需要留余地。 |
| `anchor-bypass-replace-result` | 0ms | 锚点矩阵 | 验证锚点组合 `bypass-replace-result` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-excise-method` | 0ms | 锚点矩阵 | 验证锚点组合 `excise-method` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-field-get-graft` | 0ms | 锚点矩阵 | 验证锚点组合 `field-get-graft` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-field-put-graft` | 0ms | 锚点矩阵 | 验证锚点组合 `field-put-graft` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-head-lead` | 0ms | 锚点矩阵 | 验证锚点组合 `head-lead` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-invoke-graft-after-ordinal-0` | 0ms | 锚点矩阵 | 验证锚点组合 `invoke-graft-after-ordinal-0` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-invoke-graft-before-ordinal-0` | 0ms | 锚点矩阵 | 验证锚点组合 `invoke-graft-before-ordinal-0` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-invoke-graft-before-ordinal-1` | 0ms | 锚点矩阵 | 验证锚点组合 `invoke-graft-before-ordinal-1` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-invoke-graft-before-ordinal-all` | 0ms | 锚点矩阵 | 验证锚点组合 `invoke-graft-before-ordinal-all` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-new-graft` | 0ms | 锚点矩阵 | 验证锚点组合 `new-graft` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-return-trail-multi` | 0ms | 锚点矩阵 | 验证锚点组合 `return-trail-multi` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-splice-proceed` | 0ms | 锚点矩阵 | 验证锚点组合 `splice-proceed` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-tail-trail` | 0ms | 锚点矩阵 | 验证锚点组合 `tail-trail` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-throw-graft` | 0ms | 锚点矩阵 | 验证锚点组合 `throw-graft` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-throw-trail` | 0ms | 锚点矩阵 | 验证锚点组合 `throw-trail` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-trim-arg-0` | 0ms | 锚点矩阵 | 验证锚点组合 `trim-arg-0` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-trim-arg-1` | 0ms | 锚点矩阵 | 验证锚点组合 `trim-arg-1` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `anchor-trim-return` | 0ms | 锚点矩阵 | 验证锚点组合 `trim-return` 的织入行为 | 适合确认 anchor × advice × ordinal/shift 的真实落点。 | 仍依赖 fixture 当前字节码结构，源码改动后要同步更新。 |
| `extras-descriptor-no-paren` | 0ms | 补盲边角场景 | 验证扩展场景 `descriptor-no-paren` 的补充行为 | 用于补齐主矩阵之外但真实易坏的边角场景。 | 条目较杂，阅读时要结合 fixture 名称理解上下文。 |
| `extras-excise-body-skipped` | 0ms | 补盲边角场景 | 验证扩展场景 `excise-body-skipped` 的补充行为 | 用于补齐主矩阵之外但真实易坏的边角场景。 | 条目较杂，阅读时要结合 fixture 名称理解上下文。 |
| `extras-field-get-after` | 0ms | 补盲边角场景 | 验证扩展场景 `field-get-after` 的补充行为 | 用于补齐主矩阵之外但真实易坏的边角场景。 | 条目较杂，阅读时要结合 fixture 名称理解上下文。 |
| `extras-field-get-before` | 0ms | 补盲边角场景 | 验证扩展场景 `field-get-before` 的补充行为 | 用于补齐主矩阵之外但真实易坏的边角场景。 | 条目较杂，阅读时要结合 fixture 名称理解上下文。 |
| `extras-field-put-after` | 0ms | 补盲边角场景 | 验证扩展场景 `field-put-after` 的补充行为 | 用于补齐主矩阵之外但真实易坏的边角场景。 | 条目较杂，阅读时要结合 fixture 名称理解上下文。 |
| `extras-field-put-before` | 0ms | 补盲边角场景 | 验证扩展场景 `field-put-before` 的补充行为 | 用于补齐主矩阵之外但真实易坏的边角场景。 | 条目较杂，阅读时要结合 fixture 名称理解上下文。 |
| `extras-new-after` | 0ms | 补盲边角场景 | 验证扩展场景 `new-after` 的补充行为 | 用于补齐主矩阵之外但真实易坏的边角场景。 | 条目较杂，阅读时要结合 fixture 名称理解上下文。 |
| `extras-new-before` | 0ms | 补盲边角场景 | 验证扩展场景 `new-before` 的补充行为 | 用于补齐主矩阵之外但真实易坏的边角场景。 | 条目较杂，阅读时要结合 fixture 名称理解上下文。 |
| `extras-splice-skip-short-circuit` | 0ms | 补盲边角场景 | 验证扩展场景 `splice-skip-short-circuit` 的补充行为 | 用于补齐主矩阵之外但真实易坏的边角场景。 | 条目较杂，阅读时要结合 fixture 名称理解上下文。 |
| `extras-trim-triple-012` | 0ms | 补盲边角场景 | 验证扩展场景 `trim-triple-012` 的补充行为 | 用于补齐主矩阵之外但真实易坏的边角场景。 | 条目较杂，阅读时要结合 fixture 名称理解上下文。 |
| `scope-bypass-full-descriptor` | 0ms | Scope 选择器矩阵 | 验证 scope 场景 `bypass-full-descriptor` 的目标筛选行为 | 能快速发现完整描述符、通配和组合条件的匹配回退问题。 | 更偏选择器语义，不覆盖复杂运行时谓词。 |
| `scope-graft-full-descriptor` | 0ms | Scope 选择器矩阵 | 验证 scope 场景 `graft-full-descriptor` 的目标筛选行为 | 能快速发现完整描述符、通配和组合条件的匹配回退问题。 | 更偏选择器语义，不覆盖复杂运行时谓词。 |
| `scope-lead-exact-target` | 0ms | Scope 选择器矩阵 | 验证 scope 场景 `lead-exact-target` 的目标筛选行为 | 能快速发现完整描述符、通配和组合条件的匹配回退问题。 | 更偏选择器语义，不覆盖复杂运行时谓词。 |
| `scope-lead-full-descriptor` | 0ms | Scope 选择器矩阵 | 验证 scope 场景 `lead-full-descriptor` 的目标筛选行为 | 能快速发现完整描述符、通配和组合条件的匹配回退问题。 | 更偏选择器语义，不覆盖复杂运行时谓词。 |
| `scope-lead-wildcard-args` | 0ms | Scope 选择器矩阵 | 验证 scope 场景 `lead-wildcard-args` 的目标筛选行为 | 能快速发现完整描述符、通配和组合条件的匹配回退问题。 | 更偏选择器语义，不覆盖复杂运行时谓词。 |
| `scope-splice-full` | 0ms | Scope 选择器矩阵 | 验证 scope 场景 `splice-full` 的目标筛选行为 | 能快速发现完整描述符、通配和组合条件的匹配回退问题。 | 更偏选择器语义，不覆盖复杂运行时谓词。 |
| `scope-trail-class-prefix-a` | 0ms | Scope 选择器矩阵 | 验证 scope 场景 `trail-class-prefix-a` 的目标筛选行为 | 能快速发现完整描述符、通配和组合条件的匹配回退问题。 | 更偏选择器语义，不覆盖复杂运行时谓词。 |
| `scope-trail-class-prefix-b` | 0ms | Scope 选择器矩阵 | 验证 scope 场景 `trail-class-prefix-b` 的目标筛选行为 | 能快速发现完整描述符、通配和组合条件的匹配回退问题。 | 更偏选择器语义，不覆盖复杂运行时谓词。 |
| `scope-trim-full-descriptor` | 0ms | Scope 选择器矩阵 | 验证 scope 场景 `trim-full-descriptor` 的目标筛选行为 | 能快速发现完整描述符、通配和组合条件的匹配回退问题。 | 更偏选择器语义，不覆盖复杂运行时谓词。 |
| `site-anchor-head` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `anchor-head` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-anchor-return` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `anchor-return` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-anchor-tail` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `anchor-tail` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-anchor-throw` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `anchor-throw` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-field-get` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `field-get` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-field-put` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `field-put` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-invoke-ordinal-0` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `invoke-ordinal-0` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-invoke-ordinal-1` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `invoke-ordinal-1` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-invoke-ordinal-2` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `invoke-ordinal-2` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-invoke-ordinal-all` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `invoke-ordinal-all` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-new-empty-target` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `new-empty-target` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-new-with-target` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `new-with-target` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-offset-zero` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `offset-zero` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-shift-after` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `shift-after` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `site-shift-before` | 0ms | @Site 参数矩阵 | 验证 @Site 场景 `shift-before` 的定位行为 | 专门回归 Site 注解本身的参数解析与落点选择。 | 只验证 Site，不替代更高层的 advice 组合测试。 |
| `trauma-anchor-invalid-name` | 0ms | 错误诊断回归 | 验证诊断场景 `anchor-invalid-name` 会产生预期错误信息 | 确保坏声明不会静默吞掉，方便定位用户配置错误。 | 错误文案可能随实现细节调整，断言需要留余地。 |
| `trauma-bad-descriptor` | 0ms | 错误诊断回归 | 验证诊断场景 `bad-descriptor` 会产生预期错误信息 | 确保坏声明不会静默吞掉，方便定位用户配置错误。 | 错误文案可能随实现细节调整，断言需要留余地。 |
| `trauma-bad-scope` | 0ms | 错误诊断回归 | 验证诊断场景 `bad-scope` 会产生预期错误信息 | 确保坏声明不会静默吞掉，方便定位用户配置错误。 | 错误文案可能随实现细节调整，断言需要留余地。 |
| `trauma-class-not-found` | 0ms | 错误诊断回归 | 验证诊断场景 `class-not-found` 会产生预期错误信息 | 确保坏声明不会静默吞掉，方便定位用户配置错误。 | 错误文案可能随实现细节调整，断言需要留余地。 |
| `trauma-target-illegal-chars` | 0ms | 错误诊断回归 | 验证诊断场景 `target-illegal-chars` 会产生预期错误信息 | 确保坏声明不会静默吞掉，方便定位用户配置错误。 | 错误文案可能随实现细节调整，断言需要留余地。 |
| `trim-arg-double-0` | 0ms | Trim 值改写矩阵 | 验证 Trim 场景 `arg-double-0` 的值改写行为 | 可把参数位点和返回值位点拆开回归，问题边界清晰。 | 局部变量改写等更复杂场景仍需专门 fixture。 |
| `trim-arg-double-1` | 0ms | Trim 值改写矩阵 | 验证 Trim 场景 `arg-double-1` 的值改写行为 | 可把参数位点和返回值位点拆开回归，问题边界清晰。 | 局部变量改写等更复杂场景仍需专门 fixture。 |
| `trim-arg-quad-3` | 0ms | Trim 值改写矩阵 | 验证 Trim 场景 `arg-quad-3` 的值改写行为 | 可把参数位点和返回值位点拆开回归，问题边界清晰。 | 局部变量改写等更复杂场景仍需专门 fixture。 |
| `trim-arg-single` | 0ms | Trim 值改写矩阵 | 验证 Trim 场景 `arg-single` 的值改写行为 | 可把参数位点和返回值位点拆开回归，问题边界清晰。 | 局部变量改写等更复杂场景仍需专门 fixture。 |
| `trim-arg-triple-2` | 0ms | Trim 值改写矩阵 | 验证 Trim 场景 `arg-triple-2` 的值改写行为 | 可把参数位点和返回值位点拆开回归，问题边界清晰。 | 局部变量改写等更复杂场景仍需专门 fixture。 |
| `trim-return-bool` | 0ms | Trim 值改写矩阵 | 验证 Trim 场景 `return-bool` 的值改写行为 | 可把参数位点和返回值位点拆开回归，问题边界清晰。 | 局部变量改写等更复杂场景仍需专门 fixture。 |
| `trim-return-int` | 0ms | Trim 值改写矩阵 | 验证 Trim 场景 `return-int` 的值改写行为 | 可把参数位点和返回值位点拆开回归，问题边界清晰。 | 局部变量改写等更复杂场景仍需专门 fixture。 |
| `trim-return-long` | 0ms | Trim 值改写矩阵 | 验证 Trim 场景 `return-long` 的值改写行为 | 可把参数位点和返回值位点拆开回归，问题边界清晰。 | 局部变量改写等更复杂场景仍需专门 fixture。 |
| `trim-return-string` | 0ms | Trim 值改写矩阵 | 验证 Trim 场景 `return-string` 的值改写行为 | 可把参数位点和返回值位点拆开回归，问题边界清晰。 | 局部变量改写等更复杂场景仍需专门 fixture。 |
