package top.maplex.incisiontest.fixture

/**
 * [top.maplex.incisiontest.cases.SurgeonOperationCases] 专属 fixture。
 *
 * 覆盖 @Operation 全部属性以及 @Surgeon 默认优先级与方法级覆盖：
 * - customIdTarget         : @Operation(id="...") 自定义 id
 * - disabledTarget         : @Operation(enabled=false) 默认禁用 + 手动 resume
 * - priorityTarget         : @Operation(priority=...) 优先级覆盖（高 vs 低）
 * - surgeonDefaultTarget   : @Surgeon(priority=N) 默认继承 vs @Operation(priority=...) 覆盖
 * - multiSurgeonTarget     : 不同 @Surgeon priority 的 advice 协作排序
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class SurgeonOperationTargetFixture {

    fun customIdTarget(): Int = 1

    fun disabledTarget(): Int = 1

    fun priorityTarget(): MutableList<String> = mutableListOf()

    /** 用于演示 `@Operation(priority)` 覆盖 `@Surgeon(priority)` 默认值。 */
    fun surgeonDefaultTarget(): MutableList<String> = mutableListOf()

    /** 用于演示多个 @Surgeon (不同 priority) 的 advice 排序协作。 */
    fun multiSurgeonTarget(): MutableList<String> = mutableListOf()
}
