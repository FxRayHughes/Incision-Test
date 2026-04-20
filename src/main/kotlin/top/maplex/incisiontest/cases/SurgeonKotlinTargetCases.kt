package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.KotlinTarget
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Operation
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * @KotlinTarget 测试 —— 验证 companion 实例 / @JvmStatic 静态桥两条路径，
 * 以及二者并存时 advice 是否能在两条入口都生效。
 *
 * 目标类：[top.maplex.incisiontest.fixture.SurgeonKotlinTargetFixture]
 */
@Surgeon
object SurgeonKotlinTargetCases {

    private const val FIX = "top.maplex.incisiontest.fixture.SurgeonKotlinTargetFixture"

    @Volatile var companionOnlyHits = 0
    @Volatile var staticOnlyHits = 0
    @Volatile var bothCompanionPathHits = 0
    @Volatile var bothStaticPathHits = 0

    /**
     * 仅 companionInstance —— 织入 companion 实例方法。
     * 调用 `Companion.companionOnlyEcho(...)` 时应命中。
     */
    @Lead(scope = "method:$FIX#companionOnlyEcho(java.lang.String)java.lang.String")
    @KotlinTarget(companionInstance = true)
    fun onCompanionOnly(theatre: Theatre) {
        companionOnlyHits++
    }

    /**
     * 仅 jvmStaticBridge —— 织入外部类静态桥。
     * 调用 `SurgeonKotlinTargetFixture.staticOnlyEcho(...)` 时应命中。
     */
    @Lead(scope = "method:$FIX#staticOnlyEcho(java.lang.String)java.lang.String")
    @KotlinTarget(jvmStaticBridge = true)
    fun onStaticOnly(theatre: Theatre) {
        staticOnlyHits++
    }

    /**
     * 同时开启 —— 静态桥与 companion 实例两条路径都要织入。
     * 用 [Operation] 拆成两条独立 advice，避免单条 advice 在同次调用里
     * 被两个 weave 入口各自计数造成串味。
     *
     * 静态桥路径 advice。
     */
    @Lead(scope = "method:$FIX#bothEcho(java.lang.String)java.lang.String")
    @KotlinTarget(jvmStaticBridge = true)
    @Operation(id = "both-static-path")
    fun onBothStatic(theatre: Theatre) {
        bothStaticPathHits++
    }

    /** companion 实例路径 advice。 */
    @Lead(scope = "method:$FIX#bothEcho(java.lang.String)java.lang.String")
    @KotlinTarget(companionInstance = true)
    @Operation(id = "both-companion-path")
    fun onBothCompanion(theatre: Theatre) {
        bothCompanionPathHits++
    }

    fun reset() {
        companionOnlyHits = 0
        staticOnlyHits = 0
        bothCompanionPathHits = 0
        bothStaticPathHits = 0
    }
}
