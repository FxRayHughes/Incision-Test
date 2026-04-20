package top.maplex.incisiontest

/**
 * 测试用例文档。
 *
 * 约定：
 * 1. [summary] 是唯一会出现在控制台里的简介，保持一句话即可。
 * 2. [advantage] 与 [limitation] 只保存在源码里，方便维护测试矩阵，不污染运行输出。
 * 3. 大部分案例按前缀归类，这样新增同类用例时只需要补一条规则即可。
 */
data class CaseDoc(
    val summary: String,
    val advantage: String,
    val limitation: String,
)

private data class CaseDocRule(
    val match: (String) -> Boolean,
    val summary: (String) -> String,
    val advantage: String,
    val limitation: String,
)

object CaseDocs {

    private val exact: Map<String, CaseDoc> = mapOf(
        "lead" to doc(
            "验证最基础的前置切入在方法入口触发一次",
            "最适合做轻量前置探针，断言入口织入是否成立。",
            "只能观察或改上下文，不能替代整段方法控制流。"
        ),
        "trail" to doc(
            "验证尾部切入在正常返回路径触发一次",
            "可以稳定覆盖方法退出点，适合确认收尾 advice 是否注册成功。",
            "默认关注出口语义，不适合表达中段字节码锚点。"
        ),
        "trail-throw" to doc(
            "验证 Trail 在异常出口也能被触发",
            "能同时覆盖 return 与 throw，两条出口语义清晰。",
            "只验证出口时机，不负责中间指令级匹配。"
        ),
        "splice-proceed" to doc(
            "验证环绕 advice 放行原逻辑后仍能拿到正确返回值",
            "可以直接覆盖最常见的 around + proceed 场景。",
            "只证明放行链路正确，不代表短路链路也正确。"
        ),
        "splice-skip" to doc(
            "验证环绕 advice 可以短路原方法并直接返回",
            "能快速确认 override/short-circuit 是否生效。",
            "不覆盖 proceed 后继续修改结果的复合路径。"
        ),
        "splice-modify-args" to doc(
            "验证环绕 advice 放行前改写入参",
            "同时覆盖参数改写与 proceed(args...) 两段核心能力。",
            "焦点在参数通道，不校验返回值二次改写。"
        ),
        "bypass" to doc(
            "验证对单条调用指令做 redirect 替换",
            "能准确证明 Bypass 只替换目标调用而不重写整段方法。",
            "只适合可识别的单点调用，不适合整段控制流替换。"
        ),
        "excise" to doc(
            "验证整段方法体被 Excise 完全替换",
            "适合确认 overwrite 语义和原方法体彻底不执行。",
            "一个目标只能存在一个 Excise，冲突成本最高。"
        ),
        "wildcard" to doc(
            "验证 scope 通配参数写法能够命中目标方法",
            "适合检验 descriptor 通配语法是否按预期生效。",
            "只证明匹配范围，不证明重载分辨的精确性。"
        ),
        "static-target" to doc(
            "验证静态方法目标也能被正常织入",
            "覆盖非实例目标，能及时发现 self 为空的分派问题。",
            "只验证静态方法，不涵盖 companion/@JvmStatic 变体。"
        ),
        "void-target" to doc(
            "验证无返回值方法在织入后仍可正常执行",
            "能覆盖 `void`/`Unit` 目标的桥接与收尾逻辑。",
            "不验证返回值改写类能力。"
        ),
        "primitive-int" to doc(
            "验证 `int` 返回值在织入后保持正确",
            "能覆盖最常见原始类型桥接。",
            "只覆盖 `int`，不代表所有原始类型都已覆盖。"
        ),
        "primitive-long" to doc(
            "验证 `long` 返回值在织入后保持正确",
            "能覆盖双槽原始类型返回值的桥接风险。",
            "只验证 `long` 单场景，不覆盖更复杂的宽类型组合。"
        ),
        "primitive-bool" to doc(
            "验证 `boolean` 返回值在织入后保持正确",
            "适合发现布尔值装箱与拆箱路径问题。",
            "不覆盖枚举、对象或数组返回值。"
        ),
        "multi-arg" to doc(
            "验证多参数目标在织入后按原顺序传递",
            "能同时观察多参数装配、顺序和类型桥接。",
            "只覆盖固定参数列表，不验证 vararg。"
        ),
        "priority-order" to doc(
            "验证多个 DSL advice 的优先级执行顺序",
            "能直接暴露排序与稳定顺序问题。",
            "只覆盖当前注册集合，不代表跨类聚合顺序已完全覆盖。"
        ),
        "on-batch" to doc(
            "验证一组目标批量注册后都能生效",
            "适合检查批量挂载路径与统一 heal 行为。",
            "重心在批量注册，不细分单个目标的边界条件。"
        ),
        "transient-use" to doc(
            "验证 transient 手术仅在作用域内短暂生效",
            "能快速发现作用域泄漏问题。",
            "只证明一次性使用场景，不代表长期 Suture 生命周期无误。"
        ),
        "scoped" to doc(
            "验证带 scope 约束的 DSL 手术只作用于指定目标",
            "非常适合确认选择器没有误伤其他方法。",
            "scope 表达式仍受当前 DSL 能力边界限制。"
        ),
        "threadlocal-active" to doc(
            "验证启用状态会正确进入 ThreadLocal 上下文",
            "能发现运行时上下文透传是否缺失。",
            "只覆盖激活路径，不说明线程切换后的跨线程语义。"
        ),
        "threadlocal-inactive" to doc(
            "验证未启用时不会残留 ThreadLocal 上下文",
            "能及时发现作用域清理失败。",
            "不覆盖复杂嵌套切入造成的上下文复用问题。"
        ),
        "armon-disarmon" to doc(
            "验证 arm/disarm 生命周期切换后命中状态正确",
            "能覆盖显式启停接口的关键状态转换。",
            "只覆盖线性切换，不覆盖并发切换。"
        ),
        "exclusive" to doc(
            "验证互斥手术不会与其他手术重复生效",
            "有助于发现重复命中和冲突注册。",
            "只验证互斥结果，不解释冲突解决策略的全部细节。"
        ),
        "heal" to doc(
            "验证单个 Suture heal 后立即失效",
            "能快速确认卸载链路有效。",
            "只验证单体回收，不验证批量回收。"
        ),
        "suspend-resume" to doc(
            "验证 suspend/resume 能在运行期切换手术启用状态",
            "适合确认启停不会丢失注册信息。",
            "不证明多次嵌套 suspend 的计数语义。"
        ),
        "find-list" to doc(
            "验证已注册手术可以被查询与列出",
            "适合做运维与诊断能力回归。",
            "只检查可见性，不校验查询结果的完整元数据。"
        ),
        "healall" to doc(
            "验证批量 healAll 会清空当前注册手术",
            "能一次性覆盖全局回收入口。",
            "如果存在跨作用域注册，这个用例不会替你分析来源。"
        ),
        "multi-target" to doc(
            "验证一条手术可以同时命中多个目标方法",
            "能确认 scope 扩展到多目标时仍保持稳定。",
            "只适合语义相近的目标，不验证异构签名。"
        ),
        "auto-close" to doc(
            "验证 AutoCloseable/作用域关闭时手术自动回收",
            "适合确认 try-with-resources 风格的生命周期管理。",
            "只覆盖正常关闭路径，不覆盖资源泄漏排查。"
        ),
        "duplicate-id" to doc(
            "验证重复 operation id 会被系统识别并处理",
            "能提前暴露命名冲突导致的覆盖问题。",
            "只关注重复 id，不覆盖 scope 冲突等其他冲突类型。"
        ),
        "bad-descriptor" to doc(
            "验证错误描述符输入会走诊断路径而不是静默成功",
            "能保证非法声明尽早失败，便于定位。",
            "只说明解析失败路径，不说明所有错误都具备同级诊断质量。"
        )
    )

    /**
     * 分组文档：
     * - summary 负责给控制台一个能看懂的中文解释。
     * - advantage / limitation 只用于源码维护说明，不参与输出。
     */
    private val rules = listOf(
        prefix(
            "surgeon-aop-",
            { name -> "验证注解式 AOP 场景 `${suffix(name, "surgeon-aop-")}` 的织入行为" },
            "贴近使用者真实写法，最适合回归注解扫描、注册和调用链。",
            "主要覆盖 AOP 风格接口，不等同于低层 Site/InsnPattern 全覆盖。"
        ),
        prefix(
            "surgeon-mixin-",
            { name -> "验证注解式 Mixin 场景 `${suffix(name, "surgeon-mixin-")}` 的替换行为" },
            "能直接对应 overwrite/redirect 语义，问题定位直观。",
            "只覆盖被测试的 mixin 语义，不代表全部 anchor 组合都正确。"
        ),
        prefix(
            "surgeon-graft-",
            { name -> "验证注解式 Graft 场景 `${suffix(name, "surgeon-graft-")}` 的植入行为" },
            "适合检查 INVOKE/FIELD/NEW 等中段锚点的注解写法。",
            "焦点在注解入口，不展开验证更复杂的 offset 组合。"
        ),
        prefix(
            "surgeon-operation-",
            { name -> "验证注解式 @Operation 场景 `${suffix(name, "surgeon-operation-")}` 的注册行为" },
            "能同时覆盖 id、enabled、priority 等元信息解析。",
            "只验证单类注解声明，不替代全局调度顺序测试。"
        ),
        prefix(
            "surgeon-priority-",
            { name -> "验证注解式 @Surgeon 优先级场景 `${suffix(name, "surgeon-priority-")}`" },
            "适合发现类级默认优先级的排序与覆盖问题。",
            "只验证注解默认值与排序，不覆盖运行时 suspend/resume。"
        ),
        prefix(
            "surgeon-offset-",
            { name -> "验证注解式 offset 场景 `${suffix(name, "surgeon-offset-")}` 的定位行为" },
            "能确认 annotation -> SiteSpec 的偏移转换正确。",
            "只覆盖当前 fixture 的字节码形态。"
        ),
        prefix(
            "surgeon-where-",
            { name -> "验证注解式 where 谓词 `${suffix(name, "surgeon-where-")}` 的筛选行为" },
            "能证明注解 where 与运行时谓词引擎真正串起来了。",
            "where 只在匹配事件上求值，不替代 scope/descriptor 本身。"
        ),
        prefix(
            "surgeon-bypass-",
            { name -> "验证注解式 Bypass 场景 `${suffix(name, "surgeon-bypass-")}` 的替换行为" },
            "能快速回归 redirect 风格的中段替换。",
            "仅覆盖命中的调用点，不验证整段方法覆盖。"
        ),
        prefix(
            "surgeon-trim-",
            { name -> "验证注解式 Trim 场景 `${suffix(name, "surgeon-trim-")}` 的改写行为" },
            "适合断言参数或返回值在注解模式下被改写。",
            "不覆盖局部变量全部槽位与全部类型组合。"
        ),
        prefix(
            "surgeon-",
            { name -> "验证注解式 Surgeon 基础场景 `${suffix(name, "surgeon-")}` 的织入行为" },
            "覆盖最核心的注解扫描到注册全链路。",
            "更强调入口正确性，不等同于字节码矩阵覆盖。"
        ),
        prefix(
            "kotlintarget-",
            { name -> "验证 @KotlinTarget 场景 `${suffix(name, "kotlintarget-")}` 的目标扩展行为" },
            "能区分 companion 实例方法与 @JvmStatic 桥接方法。",
            "依赖 Kotlin 生成代码形态，跨编译器版本时需警惕差异。"
        ),
        prefix(
            "ktarget-",
            { name -> "验证 KotlinTarget 矩阵场景 `${suffix(name, "ktarget-")}` 的覆盖结果" },
            "适合对四象限组合做回归，不容易漏格。",
            "主要验证目标扩展矩阵，不验证 advice 自身逻辑复杂度。"
        ),
        prefix(
            "op-matrix-",
            { name -> "验证 @Operation 三维矩阵场景 `${suffix(name, "op-matrix-")}`" },
            "能系统性回归 id、enabled、priority 的组合关系。",
            "矩阵覆盖面广，但每个格子本身都保持轻量，不深入业务逻辑。"
        ),
        prefix(
            "op-id-",
            { name -> "验证 @Operation id 场景 `${suffix(name, "op-id-")}` 的命名行为" },
            "专门盯住 operation id 的解析、保留与回退规则。",
            "只关注命名，不关注执行顺序。"
        ),
        prefix(
            "op-enpri-",
            { name -> "验证 @Operation enabled/priority 场景 `${suffix(name, "op-enpri-")}`" },
            "能快速看出启用开关与优先级叠加是否正确。",
            "只验证二维矩阵，不覆盖自定义 id。"
        ),
        prefix(
            "op-",
            { name -> "验证 Operation 相关场景 `${suffix(name, "op-")}` 的运行行为" },
            "适合收敛 operation 元信息的公共回归。",
            "描述偏概括，遇到特殊案例仍应补充精确文档。"
        ),
        prefix(
            "cross-cl-",
            { name -> "验证跨 ClassLoader 场景 `${suffix(name, "cross-cl-")}` 的分派行为" },
            "能提前发现 bridge、上下文类加载器和宿主切换问题。",
            "强依赖测试运行环境，问题复现通常只在特定平台出现。"
        ),
        prefix(
            "bukkit-",
            { name -> "验证 Bukkit API 场景 `${suffix(name, "bukkit-")}` 的织入行为" },
            "直接贴近真实 Bukkit 平台方法，价值高。",
            "依赖 Bukkit 实际运行环境，脱离平台无法说明全部结果。"
        ),
        prefix(
            "nms-",
            { name -> "验证 NMS 场景 `${suffix(name, "nms-")}` 的 remap/织入行为" },
            "能覆盖最容易出问题的服务端内部类路径。",
            "高度依赖具体服务端版本与 remap 结果。"
        ),
        prefix(
            "version-",
            { name -> "验证版本匹配场景 `${suffix(name, "version-")}` 的筛选行为" },
            "能确认 VersionMatcher、区间边界与缓存逻辑是否一致。",
            "只覆盖定义的 matcher 语义，不保证外部 matcher 实现质量。"
        ),
        prefix(
            "remap-",
            { name -> "验证 remap 场景 `${suffix(name, "remap-")}` 的翻译行为" },
            "适合定位 owner/name/desc 的映射失配问题。",
            "只说明映射结果，不验证最终织入后的运行行为。"
        ),
        prefix(
            "where-",
            { name -> "验证 where 谓词 `${suffix(name, "where-")}` 的解析与求值行为" },
            "能把谓词 DSL 的语法与运行效果拆开单测，定位快。",
            "重点是谓词引擎，不代表 advice 侧所有绑定都已覆盖。"
        ),
        prefix(
            "anchor-",
            { name -> "验证锚点组合 `${suffix(name, "anchor-")}` 的织入行为" },
            "适合确认 anchor × advice × ordinal/shift 的真实落点。",
            "仍依赖 fixture 当前字节码结构，源码改动后要同步更新。"
        ),
        prefix(
            "site-",
            { name -> "验证 @Site 场景 `${suffix(name, "site-")}` 的定位行为" },
            "专门回归 Site 注解本身的参数解析与落点选择。",
            "只验证 Site，不替代更高层的 advice 组合测试。"
        ),
        prefix(
            "scope-",
            { name -> "验证 scope 场景 `${suffix(name, "scope-")}` 的目标筛选行为" },
            "能快速发现完整描述符、通配和组合条件的匹配回退问题。",
            "更偏选择器语义，不覆盖复杂运行时谓词。"
        ),
        prefix(
            "trim-",
            { name -> "验证 Trim 场景 `${suffix(name, "trim-")}` 的值改写行为" },
            "可把参数位点和返回值位点拆开回归，问题边界清晰。",
            "局部变量改写等更复杂场景仍需专门 fixture。"
        ),
        prefix(
            "priority-",
            { name -> "验证优先级场景 `${suffix(name, "priority-")}` 的执行顺序" },
            "直接观察排序结果，能及时暴露稳定序问题。",
            "排序正确不等于 advice 语义本身正确。"
        ),
        prefix(
            "surgerydesk-",
            { name -> "验证 @SurgeryDesk 场景 `${suffix(name, "surgerydesk-")}` 的调用约束" },
            "能保证 transient DSL 的调用方约束被真正执行。",
            "只聚焦调用入口，不涵盖所有 DSL 操作。"
        ),
        prefix(
            "desk-",
            { name -> "验证不同 SurgeryDesk 调用者 `${suffix(name, "desk-")}` 的优先级与作用域" },
            "适合回归多个 desk object 并存时的行为。",
            "只覆盖当前 desk fixture，不等于跨模块调用都安全。"
        ),
        prefix(
            "trauma-",
            { name -> "验证诊断场景 `${suffix(name, "trauma-")}` 会产生预期错误信息" },
            "确保坏声明不会静默吞掉，方便定位用户配置错误。",
            "错误文案可能随实现细节调整，断言需要留余地。"
        ),
        prefix(
            "insn-pattern-",
            { name -> "验证 InsnPattern 场景 `${suffix(name, "insn-pattern-")}` 的字节码匹配结果" },
            "直接覆盖真实 opcode 序列，是定位匹配器问题的核心样本。",
            "高度依赖编译后字节码形态，源码微调就可能改变期望。"
        ),
        prefix(
            "opcodeseq-",
            { name -> "验证 OpcodeSeq 场景 `${suffix(name, "opcodeseq-")}` 的落点行为" },
            "适合检查 head/invoke 等定位器与序列匹配协作是否正确。",
            "只覆盖定义好的序列样本，不代表所有 opcode 组合。"
        ),
        prefix(
            "offset-",
            { name -> "验证 offset 场景 `${suffix(name, "offset-")}` 的相对偏移行为" },
            "能直接发现 BEFORE/AFTER 与正负 offset 的实现偏差。",
            "依赖具体指令序列，换字节码后需要同步维护。"
        ),
        prefix(
            "extras-",
            { name -> "验证扩展场景 `${suffix(name, "extras-")}` 的补充行为" },
            "用于补齐主矩阵之外但真实易坏的边角场景。",
            "条目较杂，阅读时要结合 fixture 名称理解上下文。"
        ),
        prefix(
            "accessor-",
            { name -> "验证 Accessor API `${suffix(name, "accessor-")}` 的字段/方法访问能力" },
            "覆盖 private/final/static/继承链等各种访问级别，确保 JVMTI→反射→Unsafe 三级 fallback 正确。",
            "依赖 JVMTI native 可用性；JIT 常量折叠场景不做硬断言。"
        ),
        prefix(
            "util-",
            { name -> "验证 Theatre 工具方法 `${suffix(name, "util-")}` 的便捷访问能力" },
            "覆盖 arg/cast/selfAs/readField/writeField/callMethod 等常用工具，确保类型安全与边界行为。",
            "工具方法底层委派到 IncisionAccessor，JVMTI 不可用时降级行为同 accessor 测试。"
        )
    )

    fun of(name: String): CaseDoc {
        exact[name]?.let { return it }
        rules.firstOrNull { it.match(name) }?.let { rule ->
            return CaseDoc(rule.summary(name), rule.advantage, rule.limitation)
        }
        return doc(
            "验证 `$name` 的织入与运行行为",
            "保底文档能避免新增用例在列表里完全失去说明。",
            "未命中分组规则时说明较泛化，后续应补精确文档。"
        )
    }

    private fun doc(summary: String, advantage: String, limitation: String): CaseDoc {
        return CaseDoc(summary, advantage, limitation)
    }

    private fun prefix(
        prefix: String,
        summary: (String) -> String,
        advantage: String,
        limitation: String,
    ): CaseDocRule {
        return CaseDocRule(
            match = { it.startsWith(prefix) },
            summary = summary,
            advantage = advantage,
            limitation = limitation
        )
    }

    private fun suffix(name: String, prefix: String): String {
        return name.removePrefix(prefix).ifBlank { name }
    }
}
