package top.maplex.incisiontest

import taboolib.common.platform.ProxyCommandSender
import top.maplex.incisiontest.fixture.TargetFixture

/**
 * 简易断言框架 —— 所有测试用例共享。
 *
 * 每个 case 返回一个 [CaseResult]，由 [TestRunner] 汇总输出。
 */
data class CaseResult(val name: String, val ok: Boolean, val detail: String, val ms: Long)

class Asserts {
    private val lines = StringBuilder()
    var ok = true
        private set

    fun check(cond: Boolean, msg: String) {
        if (!cond) {
            ok = false
            lines.appendLine("  FAIL: $msg")
        }
    }

    fun <T> equal(expected: T, actual: T, label: String) {
        check(expected == actual, "$label expected=<$expected> actual=<$actual>")
    }

    fun isNull(value: Any?, label: String) {
        check(value == null, "$label expected=null actual=<$value>")
    }

    fun notNull(value: Any?, label: String) {
        check(value != null, "$label expected=non-null actual=null")
    }

    fun isTrue(value: Boolean, label: String) {
        check(value, "$label expected=true actual=false")
    }

    fun isFalse(value: Boolean, label: String) {
        check(!value, "$label expected=false actual=true")
    }

    fun note(msg: String) {
        lines.appendLine("  NOTE: $msg")
    }

    fun detail(): String = lines.toString().trim()
}

inline fun runCase(name: String, block: (Asserts, TargetFixture) -> Unit): CaseResult {
    val a = Asserts()
    val fx = TargetFixture()
    val t0 = System.nanoTime()
    val err = runCatching { block(a, fx) }.exceptionOrNull()
    val ms = (System.nanoTime() - t0) / 1_000_000
    if (err != null) {
        a.check(false, "exception: ${err.javaClass.simpleName}: ${err.message}")
    }
    return CaseResult(name, a.ok, a.detail(), ms)
}

object TestRunner {

    fun runAll(sender: ProxyCommandSender) {
        val cases = AllCases.entries
        val results = cases.map { (name, fn) ->
            try {
                fn()
            } catch (t: Throwable) {
                CaseResult(name, false, "runner-error: ${t.message}", 0)
            }
        }
        report(sender, results)
    }

    fun runOne(sender: ProxyCommandSender, name: String) {
        val fn = AllCases.entries.firstOrNull { it.first.equals(name, ignoreCase = true) }?.second
        if (fn == null) {
            sender.sendMessage("§c未知用例: $name  可用: ${AllCases.entries.joinToString { it.first }}")
            return
        }
        report(sender, listOf(fn()))
    }

    fun list(sender: ProxyCommandSender) {
        sender.sendMessage("§e共 ${AllCases.entries.size} 个用例:")
        AllCases.entries.forEachIndexed { i, (n, _) ->
            sender.sendMessage("  §7${i + 1}. §f$n §8- ${CaseDocs.of(n).summary}")
        }
    }

    private fun report(sender: ProxyCommandSender, results: List<CaseResult>) {
        val pass = results.count { it.ok }
        val fail = results.size - pass
        sender.sendMessage("§6============ §eIncision Test §6============")
        for (r in results) {
            val tag = if (r.ok) "§aPASS" else "§cFAIL"
            val doc = CaseDocs.of(r.name)
            // 控制台只展示一句 summary，优点与局限仅保留在源码文档中。
            sender.sendMessage("$tag §f${r.name.padEnd(26)} §7${r.ms}ms §8- ${doc.summary}")
            if (!r.ok && r.detail.isNotBlank()) {
                r.detail.lines().forEach { sender.sendMessage("  §c$it") }
            }
        }
        sender.sendMessage("§6结果: §a$pass pass §7/ §c$fail fail §7/ §f${results.size} total")
    }
}
