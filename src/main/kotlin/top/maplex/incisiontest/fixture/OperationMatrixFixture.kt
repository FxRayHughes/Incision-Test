package top.maplex.incisiontest.fixture

/**
 * @Operation 三维组合 (id × enabled × priority) 的目标基座。
 *
 * 每个目标方法对应一个独立的组合点，advice 在 [top.maplex.incisiontest.cases.OperationMatrixCases]。
 *
 * 目标命名规则：`m{N}{IdFlag}{EnFlag}{PriTag}`
 * - IdFlag    : I = 自定义 id, _ = 默认（方法名）
 * - EnFlag    : E = enabled=true, D = enabled=false
 * - PriTag    : 数字或 "0" / "+" / "-"
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class OperationMatrixFixture {

    fun m1Default(): Int = 1                 // (id="", enabled=true, priority=0) 基线
    fun m2CustomId(): Int = 2                // (id="m2-cust", enabled=true, priority=0)
    fun m3DisabledNoId(): Int = 3            // (id="", enabled=false, priority=0)
    fun m4PriorityPositive(): Int = 4        // (id="", enabled=true, priority=10)
    fun m5PriorityNegative(): Int = 5        // (id="", enabled=true, priority=-5)
    fun m6IdAndPriority(): Int = 6           // (id="m6-cust", enabled=true, priority=20)
    fun m7IdAndDisabled(): Int = 7           // (id="m7-cust", enabled=false, priority=0)
    fun m8AllThree(): Int = 8                // (id="m8-cust", enabled=false, priority=99)
    fun m9PriorityZeroExplicit(): Int = 9    // (id="", enabled=true, priority=0) 显式 0

    /** 多 advice 共享 —— 验证 priority 顺序：高/中/低/负 */
    fun mOrderTarget(): MutableList<String> = mutableListOf()
}
