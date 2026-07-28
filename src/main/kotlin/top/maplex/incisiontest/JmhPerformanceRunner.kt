package top.maplex.incisiontest

import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import org.openjdk.jmh.runner.options.TimeValue
import taboolib.common.Inject
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.ProxyCommandSender
import taboolib.module.incision.loader.InstrumentationBackend
import taboolib.module.incision.loader.JvmtiBackend
import top.maplex.incisiontest.benchmark.IncisionJmhBenchmark
import top.maplex.incisiontest.cases.PerformanceCases
import top.maplex.incisiontest.fixture.PerformanceFixture
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 在当前 Minecraft 进程内运行 JMH。
 *
 * JMH 常规 fork 会启动一个没有 Bukkit、插件 ClassLoader 和已织入字节码的新 JVM，因此这里固定
 * forks=0。它牺牲了进程级隔离，但保留了真实服务端、真实 Backend 与真实 dispatcher 调用链；矩阵
 * 通过每个服务端/Backend 单独重启进程恢复隔离。
 */
@Inject
@RuntimeDependencies(
    RuntimeDependency(
        value = "!org.openjdk.jmh:jmh-core:1.37",
        test = "!org.openjdk.jmh.runner.Runner",
    ),
    // TabooLib 的运行时依赖解析不保证递归装入 JMH 的统计与命令行依赖；显式声明可避免
    // 服务端插件 ClassLoader 在结果聚合阶段才出现 NoClassDefFoundError，导致整轮数据作废。
    RuntimeDependency(
        value = "!org.apache.commons:commons-math3:3.6.1",
        test = "!org.apache.commons.math3.stat.descriptive.StatisticalSummary",
    ),
    RuntimeDependency(
        value = "!net.sf.jopt-simple:jopt-simple:5.0.4",
        test = "!joptsimple.OptionParser",
    ),
    RuntimeDependency(
        value = "!net.bytebuddy:byte-buddy-agent:1.14.18",
        test = "!net.bytebuddy.agent.ByteBuddyAgent",
    ),
)
object JmhPerformanceRunner {

    private const val DEFAULT_WARMUP_ITERATIONS = 3
    private const val DEFAULT_MEASUREMENT_ITERATIONS = 7
    private const val DEFAULT_ITERATION_MILLIS = 500L

    /** 与 Incision 核心一致，运行时拼接以防 TabooLib Shadow 重定位 JVM 协议键。 */
    private val backendProperty =
        String(charArrayOf('t', 'a', 'b', 'o', 'o', 'l', 'i', 'b')) + ".incision.backend"

    fun run(sender: ProxyCommandSender) {
        try {
            runChecked(sender)
        } catch (t: Throwable) {
            sender.sendMessage("JMH-FAILED backend=unknown reason=" + t.javaClass.name + ":" + t.message)
            t.printStackTrace()
        }
    }

    /** 把依赖解析与 Backend 探测也纳入失败协议，矩阵脚本才能在启动错误时立即停止节点。 */
    private fun runChecked(sender: ProxyCommandSender) {
        val backend = System.getProperty(backendProperty, "auto").lowercase(Locale.ROOT)
        val available = when (backend) {
            "instrumentation" -> InstrumentationBackend.available()
            "jvmti" -> JvmtiBackend.available()
            else -> InstrumentationBackend.available() || JvmtiBackend.available()
        }
        if (!available) {
            sender.sendMessage("JMH-FAILED backend=$backend reason=backend-unavailable")
            return
        }
        if (!verifyWeaving()) {
            sender.sendMessage("JMH-FAILED backend=$backend reason=weaving-probe-missed")
            return
        }

        val output = resolveOutput(backend)
        output.parentFile.mkdirs()
        val warmupIterations = positiveIntProperty("incision.jmh.warmupIterations", DEFAULT_WARMUP_ITERATIONS)
        val measurementIterations = positiveIntProperty("incision.jmh.measurementIterations", DEFAULT_MEASUREMENT_ITERATIONS)
        val iterationMillis = positiveLongProperty("incision.jmh.iterationMillis", DEFAULT_ITERATION_MILLIS)
        sender.sendMessage(
            "JMH-START backend=$backend unit=ns/op warmup=$warmupIterations measurement=$measurementIterations " +
                "iterationMillis=$iterationMillis forks=0 output=${output.absolutePath}"
        )

        try {
            val options = OptionsBuilder()
                .include("^${Regex.escape(IncisionJmhBenchmark::class.java.name)}\\..*$")
                .mode(org.openjdk.jmh.annotations.Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .warmupIterations(warmupIterations)
                .warmupTime(TimeValue.milliseconds(iterationMillis))
                .measurementIterations(measurementIterations)
                .measurementTime(TimeValue.milliseconds(iterationMillis))
                .threads(1)
                .forks(0)
                .shouldFailOnError(true)
                .resultFormat(ResultFormatType.JSON)
                .result(output.absolutePath)
                .build()
            val results = Runner(options).run()
            results.sortedBy { it.primaryResult.label }.forEach { result ->
                sender.sendMessage(
                    "JMH-RESULT benchmark=${result.params.benchmark.substringAfterLast('.')} " +
                        "score=${format(result.primaryResult.score)} error=${format(result.primaryResult.scoreError)} unit=${result.primaryResult.scoreUnit}"
                )
            }
            sender.sendMessage("JMH-END backend=$backend benchmarks=${results.size} output=${output.absolutePath}")
        } catch (t: Throwable) {
            sender.sendMessage("JMH-FAILED backend=$backend reason=${t.javaClass.name}:${t.message}")
            t.printStackTrace()
        }
    }

    /** 独立探针验证 advice 确实由本次强制 Backend 安装，防止把裸方法结果写入性能报告。 */
    private fun verifyWeaving(): Boolean {
        PerformanceCases.probeHits = 0
        PerformanceFixture().probe(1)
        return PerformanceCases.probeHits == 1
    }

    private fun resolveOutput(backend: String): File {
        val configured = System.getProperty("incision.jmh.output")?.takeIf { it.isNotBlank() }
        return configured?.let(::File) ?: File("plugins/Incision-Test/jmh/$backend.json")
    }

    private fun positiveIntProperty(name: String, fallback: Int): Int =
        System.getProperty(name)?.toIntOrNull()?.takeIf { it > 0 } ?: fallback

    private fun positiveLongProperty(name: String, fallback: Long): Long =
        System.getProperty(name)?.toLongOrNull()?.takeIf { it > 0L } ?: fallback

    private fun format(value: Double): String = String.format(Locale.ROOT, "%.3f", value)
}
