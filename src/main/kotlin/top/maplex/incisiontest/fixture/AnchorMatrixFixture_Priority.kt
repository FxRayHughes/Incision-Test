package top.maplex.incisiontest.fixture

/**
 * Task #13 第二批 —— priority 矩阵 fixture。
 *
 * 提供两个目标方法，分别让 @Surgeon(priority) 与 @Operation(priority)
 * 在多 advice 链上验证执行顺序与负数优先级。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class AnchorMatrixFixture_Priority {

    fun prioritySurgeonTarget(): String = "raw"
    fun priorityOperationTarget(): String = "raw"
    fun priorityNegativeTarget(): String = "raw"
}
