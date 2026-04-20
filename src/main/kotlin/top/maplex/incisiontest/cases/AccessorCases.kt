package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.*

/**
 * Accessor Lambda 工厂测试 — 验证 field / staticField / fieldSet / method 等工厂 API。
 *
 * 目标落在 [top.maplex.incisiontest.fixture.AccessorFixture]。
 */
@Surgeon
object AccessorCases {

    // ---- Lambda 工厂声明（类级，解析一次） ----

    private val privateFinalName = field<String>("privateFinalName")
    private val protectedValue = field<Double>("protectedValue")
    private val internalTag = field<String>("internalTag")
    private val publicLabel = field<String>("publicLabel")

    private val setMutableCount = fieldSet<Int>("privateMutableCount")
    private val getMutableCount = field<Int>("privateMutableCount")

    private val staticSecret = staticField<String>(
        top.maplex.incisiontest.fixture.AccessorFixture::class.java,
        "STATIC_SECRET"
    )

    private val setStaticCounter = staticFieldSet<Int>(
        top.maplex.incisiontest.fixture.AccessorFixture::class.java,
        "staticCounter"
    )

    private val getStaticCounter = staticField<Int>(
        top.maplex.incisiontest.fixture.AccessorFixture::class.java,
        "staticCounter"
    )

    private val privateAdd = method<Int>("privateAdd", "(II)I")
    private val privateGreet = method<String>("privateGreet")

    // ---- 命中记录 ----

    @Volatile var lastPrivateFinalName: String? = null
    @Volatile var lastProtectedValue: Double? = null
    @Volatile var lastInternalTag: String? = null
    @Volatile var lastPublicLabel: String? = null
    @Volatile var lastMutableCountAfterSet: Int? = null
    @Volatile var lastStaticSecret: String? = null
    @Volatile var lastStaticCounterAfterSet: Int? = null
    @Volatile var lastPrivateAddResult: Int? = null
    @Volatile var lastPrivateGreetResult: String? = null

    @Lead(scope = "method:top.maplex.incisiontest.fixture.AccessorFixture#publicDouble(int)int")
    fun onPublicDouble(t: Theatre) {
        // 读各种访问级别的实例字段
        lastPrivateFinalName = privateFinalName(t)
        lastProtectedValue = protectedValue(t)
        lastInternalTag = internalTag(t)
        lastPublicLabel = publicLabel(t)

        // 写 private mutable 字段，再读回
        setMutableCount(t, 42)
        lastMutableCountAfterSet = getMutableCount(t)

        // 读 static 字段
        lastStaticSecret = staticSecret()

        // 写 static 字段，再读回
        setStaticCounter(99)
        lastStaticCounterAfterSet = getStaticCounter()

        // 调用 private 方法
        lastPrivateAddResult = privateAdd(t, 10, 20)
        lastPrivateGreetResult = privateGreet(t, "world")
    }

    fun reset() {
        lastPrivateFinalName = null
        lastProtectedValue = null
        lastInternalTag = null
        lastPublicLabel = null
        lastMutableCountAfterSet = null
        lastStaticSecret = null
        lastStaticCounterAfterSet = null
        lastPrivateAddResult = null
        lastPrivateGreetResult = null
    }
}
