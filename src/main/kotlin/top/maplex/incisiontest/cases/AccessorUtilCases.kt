package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.*
import top.maplex.incisiontest.fixture.Greetable

/**
 * Theatre 工具方法测试 — 验证 arg / argOrThrow / cast / castOrThrow / selfAs / readField / writeField / callMethod。
 *
 * 目标落在 [top.maplex.incisiontest.fixture.UtilFixture#process] 和
 * [top.maplex.incisiontest.fixture.AccessorFixture#publicDouble]。
 */
@Surgeon
object AccessorUtilCases {

    // ---- arg / argOrThrow ----
    @Volatile var lastArgString: String? = null
    @Volatile var lastArgInt: Int? = null
    @Volatile var lastArgNull: Any? = "sentinel"
    @Volatile var lastArgOrThrowString: String? = null
    @Volatile var lastArgOutOfBounds: Boolean = false

    // ---- cast / castOrThrow ----
    @Volatile var lastCastGreetable: Greetable? = null
    @Volatile var lastCastNull: Greetable? = null
    @Volatile var lastCastOrThrowString: String? = null
    @Volatile var lastCastOrThrowFailed: Boolean = false

    // ---- selfAs ----
    @Volatile var lastSelfAsString: String? = null

    // ---- readField / writeField / callMethod ----
    @Volatile var lastReadFieldOnArg: String? = null
    @Volatile var lastCallMethodOnArg: String? = null
    @Volatile var lastWriteFieldVerify: String? = null

    @Lead(scope = "method:top.maplex.incisiontest.fixture.UtilFixture#process(java.lang.String,int,java.lang.Object)java.lang.String")
    fun onProcess(t: Theatre) {
        // ---- arg ----
        lastArgString = t.arg<String>(0)
        lastArgInt = t.arg<Int>(1)
        lastArgNull = t.arg<String>(99)  // 越界 → null

        // ---- argOrThrow ----
        lastArgOrThrowString = t.argOrThrow<String>(0)
        lastArgOutOfBounds = try {
            t.argOrThrow<String>(99)
            false
        } catch (_: IndexOutOfBoundsException) {
            true
        }

        // ---- cast / castOrThrow on args[2] ----
        val tag = t.args[2]
        lastCastGreetable = tag.cast<Greetable>()
        lastCastNull = "not-greetable".cast<Greetable>()

        lastCastOrThrowString = "hello".castOrThrow<String>()
        lastCastOrThrowFailed = try {
            42.castOrThrow<String>()
            false
        } catch (_: ClassCastException) {
            true
        }

        // ---- selfAs ----
        lastSelfAsString = t.selfAs<Any>()?.javaClass?.simpleName

        // ---- readField / callMethod on args[2] (GreetableImpl) ----
        if (tag != null) {
            lastReadFieldOnArg = tag.readField<String>("secret")
            lastCallMethodOnArg = tag.callMethod<String>("greet")

            // writeField + 读回验证
            tag.writeField("secret", "modified")
            lastWriteFieldVerify = tag.readField<String>("secret")
        }
    }

    fun reset() {
        lastArgString = null
        lastArgInt = null
        lastArgNull = "sentinel"
        lastArgOrThrowString = null
        lastArgOutOfBounds = false
        lastCastGreetable = null
        lastCastNull = null
        lastCastOrThrowString = null
        lastCastOrThrowFailed = false
        lastSelfAsString = null
        lastReadFieldOnArg = null
        lastCallMethodOnArg = null
        lastWriteFieldVerify = null
    }
}
