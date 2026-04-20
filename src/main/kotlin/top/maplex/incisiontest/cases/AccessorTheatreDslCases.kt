package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * Theatre DSL 直接调用测试 — 验证 theatre.field / theatre.staticField / theatre.setField / theatre.invoke。
 *
 * 目标落在 [top.maplex.incisiontest.fixture.AccessorChildFixture]（继承场景）。
 */
@Surgeon
object AccessorTheatreDslCases {

    @Volatile var lastChildLabel: String? = null
    @Volatile var lastParentSecret: String? = null
    @Volatile var lastParentCounterAfterSet: Int? = null

    @Lead(scope = "method:top.maplex.incisiontest.fixture.AccessorChildFixture#describe()java.lang.String")
    fun onDescribe(t: Theatre) {
        // 读子类自身字段
        lastChildLabel = t.field("childLabel")

        // 读父类 private 字段（自动沿继承链向上）
        lastParentSecret = t.field("parentSecret")

        // 写父类 protected 字段，再读回
        t.setField(
            Class.forName("top.maplex.incisiontest.fixture.AccessorParentFixture"),
            "parentCounter",
            77
        )
        lastParentCounterAfterSet = t.field(
            Class.forName("top.maplex.incisiontest.fixture.AccessorParentFixture"),
            "parentCounter"
        )
    }

    fun reset() {
        lastChildLabel = null
        lastParentSecret = null
        lastParentCounterAfterSet = null
    }
}
