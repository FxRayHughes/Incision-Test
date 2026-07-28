@file:Suppress("DEPRECATION")

package top.maplex.incisiontest.cases

import taboolib.module.incision.loader.Backend
import taboolib.module.incision.loader.ClassLoaderHookBackend
import taboolib.module.incision.loader.PipelineBackend
import top.maplex.incisiontest.CaseResult
import top.maplex.incisiontest.runCase

/**
 * Backend 公共协议测试。
 *
 * 这些用例刻意不依赖 Bukkit/NMS 类，使每个服务端和 JVM 都能验证事务状态、token 撤销和
 * 生成期 Pipeline。Instrumentation 与 JVMTI 的真实重转换仍由其余 300+ 个织入用例覆盖。
 */
object BackendContractCases {

    fun testUnavailable(): CaseResult = runCase("backend-unavailable") { asserts, _ ->
        val backend = FakeBackend(available = false, loaded = true)
        val installation = backend.install(TARGET) { it }
        asserts.equal(Backend.InstallStatus.UNAVAILABLE, installation.status, "不可用后端状态")
        asserts.isNull(installation.token, "不可用后端不得返回 token")
        asserts.equal(0, backend.registrations, "不可用后端不得注册 transformer")
    }

    fun testPendingLoad(): CaseResult = runCase("backend-pending-load") { asserts, _ ->
        val backend = FakeBackend(available = true, loaded = false)
        val installation = backend.install(TARGET) { it + 4 }
        asserts.equal(Backend.InstallStatus.PENDING_LOAD, installation.status, "未加载类状态")
        asserts.notNull(installation.token, "等待加载必须保留 transformer token")
        asserts.equal(0, backend.retransformCalls, "未加载类不得尝试重转换")
        installation.token?.remove()
        asserts.equal(1, backend.removals, "调用方可撤销等待中的 transformer")
    }

    fun testInstalled(): CaseResult = runCase("backend-installed") { asserts, _ ->
        val backend = FakeBackend(available = true, loaded = true)
        val installation = backend.install(TARGET) { it + 4 }
        asserts.equal(Backend.InstallStatus.INSTALLED, installation.status, "成功重转换状态")
        asserts.equal(listOf<Byte>(1, 2, 3, 4), backend.lastBytes?.toList(), "transformer 输出已实际应用")
        asserts.equal(0, backend.removals, "成功安装前不得撤销 token")
        installation.token?.remove()
    }

    fun testRetransformRollback(): CaseResult = runCase("backend-retransform-rollback") { asserts, _ ->
        val backend = FakeBackend(available = true, loaded = true, forceRetransformFailure = true)
        val installation = backend.install(TARGET) { it + 4 }
        asserts.equal(Backend.InstallStatus.FAILED_ROLLED_BACK, installation.status, "重转换失败状态")
        asserts.isNull(installation.token, "失败结果不得泄漏 token")
        asserts.equal(1, backend.removals, "失败必须撤销 transformer")
        // retransform=false 且 transformer 从未被调用时 JVM 字节码未变，不应做无意义的第二次重转换。
        asserts.equal(1, backend.retransformCalls, "未进入 transformer 时只需撤销注册")
    }

    fun testTransformerRollback(): CaseResult = runCase("backend-transformer-rollback") { asserts, _ ->
        val backend = FakeBackend(available = true, loaded = true)
        val installation = backend.install(TARGET) { throw IllegalStateException("verify failure") }
        asserts.equal(Backend.InstallStatus.FAILED_ROLLED_BACK, installation.status, "离线织入异常状态")
        asserts.check(installation.reason?.contains("verify failure") == true, "失败原因应保留 verifier 诊断")
        asserts.equal(1, backend.removals, "异常必须撤销 transformer")
    }

    fun testPipelineLifecycle(): CaseResult = runCase("backend-pipeline-lifecycle") { asserts, _ ->
        PipelineBackend.clear()
        val installation = PipelineBackend.install(PIPELINE_TARGET) { it + 9 }
        asserts.equal(Backend.InstallStatus.PENDING_LOAD, installation.status, "Pipeline 仅等待未来生成字节码")
        asserts.equal(listOf<Byte>(1, 2, 9), PipelineBackend.apply(PIPELINE_TARGET, byteArrayOf(1, 2)).toList(), "Pipeline 变换")
        installation.token?.remove()
        asserts.isFalse(PipelineBackend.has(PIPELINE_TARGET), "撤销最后一个 token 后必须删除空索引")
        asserts.equal(listOf<Byte>(1, 2), PipelineBackend.apply(PIPELINE_TARGET, byteArrayOf(1, 2)).toList(), "撤销后保持原字节码")
    }

    fun testClassLoaderHookUnavailable(): CaseResult = runCase("backend-classloader-hook-unavailable") { asserts, _ ->
        asserts.isFalse(ClassLoaderHookBackend.available(), "伪 ClassLoader hook 不得虚报能力")
        val installation = ClassLoaderHookBackend.install(TARGET) { it }
        asserts.equal(Backend.InstallStatus.UNAVAILABLE, installation.status, "兼容占位必须返回 UNAVAILABLE")
    }

    /** 可控后端用于逐项验证默认事务协议，不把故障注入耦合到真实 JVM 状态。 */
    private class FakeBackend(
        private val available: Boolean,
        private val loaded: Boolean,
        private val forceRetransformFailure: Boolean = false,
    ) : Backend {

        override val name: String = "FakeBackend"
        var registrations = 0
        var removals = 0
        var retransformCalls = 0
        var lastBytes: ByteArray? = null
        private var transformer: ((ByteArray) -> ByteArray?)? = null

        override fun available(): Boolean = available

        override fun addTransformer(className: String, transformer: (ByteArray) -> ByteArray?): Backend.BackendToken {
            registrations++
            this.transformer = transformer
            return object : Backend.BackendToken {
                override fun remove() {
                    removals++
                    this@FakeBackend.transformer = null
                }
            }
        }

        override fun isClassLoaded(className: String): Boolean = loaded

        override fun retransform(className: String): Boolean {
            retransformCalls++
            if (forceRetransformFailure) return false
            val active = transformer ?: return false
            return try {
                lastBytes = active(byteArrayOf(1, 2, 3))
                lastBytes != null
            } catch (_: Throwable) {
                false
            }
        }
    }

    private const val TARGET = "top/maplex/incisiontest/fixture/BackendTarget"
    private const val PIPELINE_TARGET = "top/maplex/incisiontest/generated/PipelineTarget"
}
