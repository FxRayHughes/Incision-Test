package top.maplex.incisiontest.fixture

/**
 * [top.maplex.incisiontest.cases.SurgeonMixinCases] 专属 fixture。
 * 验证 @Excise(Overwrite) 和 @Bypass(Redirect) 两种 Mixin 风格。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class SurgeonMixinTargetFixture {

    /** @Excise(Overwrite) 目标。原方法会抛异常，织入后应吞掉异常返回 "mixin-safe"。 */
    fun mayThrow(throwIt: Boolean): String {
        if (throwIt) error("boom")
        return "ok"
    }

    /** @Bypass(Redirect) 目标。原方法返回 x*10，织入后应返回固定 888。 */
    fun helper(x: Int): Int = x * 10
}
