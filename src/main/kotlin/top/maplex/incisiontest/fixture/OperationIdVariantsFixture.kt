package top.maplex.incisiontest.fixture

/**
 * @Operation(id) 变种基座 —— 4 个独立目标方法对应 4 种 id 形态。
 *
 * - normal     : 普通字符串 id
 * - special    : 含特殊字符 (`-` / `.` / `/` / `:` / `#` / `空格`) 的 id
 * - empty      : 显式 id="" — 应回退到方法名
 * - veryLong   : 极长 id（>=128 字符）
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class OperationIdVariantsFixture {

    fun idNormalTarget(): Int = 1
    fun idSpecialCharsTarget(): Int = 2
    fun idEmptyFallbackTarget(): Int = 3
    fun idVeryLongTarget(): Int = 4
}
