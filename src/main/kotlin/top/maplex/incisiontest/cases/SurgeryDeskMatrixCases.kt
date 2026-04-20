package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.SurgeryDesk
import taboolib.module.incision.dsl.Scalpel
import top.maplex.incisiontest.fixture.SurgeryDeskCallerFixture

/**
 * @SurgeryDesk 变种持有 —— 演示标注在不同 object 上、带不同 priority 都能正常调用 transient API。
 *
 * 这些 desk 不在 AllCases 内部（AllCases 自己就是 @SurgeryDesk）。
 * 为了能从 AllCases 触发它们，提供 public entry 方法供外部调用。
 *
 * - [SurgeryDeskDefaultCaller] : @SurgeryDesk()              默认 priority=0
 * - [SurgeryDeskHighCaller]    : @SurgeryDesk(priority=99)
 * - [SurgeryDeskNegativeCaller]: @SurgeryDesk(priority=-7)
 */

@SurgeryDesk
object SurgeryDeskDefaultCaller {
    fun runOnce(fx: SurgeryDeskCallerFixture): Int {
        var captured = -1
        val s = Scalpel.transient {
            lead("top.maplex.incisiontest.fixture.SurgeryDeskCallerFixture#observable(int)int") { th ->
                captured = th.args[0] as Int
            }
        }
        try {
            fx.observable(7)
            return captured
        } finally { s.heal() }
    }
}

@SurgeryDesk(priority = 99)
object SurgeryDeskHighCaller {
    fun runOnce(fx: SurgeryDeskCallerFixture): Int {
        var captured = -1
        val s = Scalpel.transient {
            lead("top.maplex.incisiontest.fixture.SurgeryDeskCallerFixture#observable(int)int") { th ->
                captured = th.args[0] as Int
            }
        }
        try {
            fx.observable(11)
            return captured
        } finally { s.heal() }
    }
}

@SurgeryDesk(priority = -7)
object SurgeryDeskNegativeCaller {
    fun runOnce(fx: SurgeryDeskCallerFixture): Int {
        var captured = -1
        val s = Scalpel.transient {
            lead("top.maplex.incisiontest.fixture.SurgeryDeskCallerFixture#observable(int)int") { th ->
                captured = th.args[0] as Int
            }
        }
        try {
            fx.observable(13)
            return captured
        } finally { s.heal() }
    }
}
