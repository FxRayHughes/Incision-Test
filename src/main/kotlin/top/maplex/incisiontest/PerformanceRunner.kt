package top.maplex.incisiontest

import taboolib.common.platform.ProxyCommandSender
import top.maplex.incisiontest.fixture.PerformanceFixture

/**
 * 进程内微基准运行器。
 *
 * 结果用于同一 JVM、同一启动内比较 Incision 各织入路径，不替代 JMH 的跨进程统计。计时只包围
 * 热循环，预热、消息输出和结果排序均在计时区间外；纳秒/次由整批耗时除以固定迭代数得到。
 */
object PerformanceRunner {

    private const val WARMUP_ROUNDS = 4
    private const val WARMUP_ITERATIONS = 250_000
    private const val SAMPLE_COUNT = 11
    private const val SAMPLE_ITERATIONS = 500_000

    /** 防止 JIT 把仅用于校验结果的数据流整体消除。 */
    @Volatile
    private var blackhole = 0L

    fun run(sender: ProxyCommandSender) {
        val fixture = PerformanceFixture()
        val benchmarks = listOf(
            Benchmark("baseline") { fixture.baseline(it) },
            Benchmark("lead") { fixture.lead(it) },
            Benchmark("lead_trail") { fixture.leadTrail(it) },
            Benchmark("predicate_true") { fixture.predicate(it) },
            Benchmark("splice_proceed") { fixture.splice(it) },
            Benchmark("site_invoke") { fixture.siteInvoke(it) },
        )

        sender.sendMessage("PERF-START unit=ns warmup=${WARMUP_ROUNDS}x$WARMUP_ITERATIONS samples=${SAMPLE_COUNT}x$SAMPLE_ITERATIONS")
        repeat(WARMUP_ROUNDS) {
            benchmarks.forEach { consume(it.call, WARMUP_ITERATIONS) }
        }
        val results = benchmarks.map { benchmark -> measure(benchmark) }
        val baseline = results.first().medianNs
        results.forEach { result ->
            sender.sendMessage(
                "PERF name=${result.name} median_ns=${format(result.medianNs)} " +
                    "p95_ns=${format(result.p95Ns)} min_ns=${format(result.minNs)} " +
                    "overhead_ns=${format(result.medianNs - baseline)} samples=$SAMPLE_COUNT iterations=$SAMPLE_ITERATIONS"
            )
        }
        sender.sendMessage("PERF-END benchmarks=${results.size} blackhole=$blackhole")
    }

    private fun measure(benchmark: Benchmark): BenchmarkResult {
        val samples = DoubleArray(SAMPLE_COUNT)
        repeat(SAMPLE_COUNT) { sample ->
            val started = System.nanoTime()
            consume(benchmark.call, SAMPLE_ITERATIONS)
            samples[sample] = (System.nanoTime() - started).toDouble() / SAMPLE_ITERATIONS
        }
        samples.sort()
        val p95Index = kotlin.math.ceil(samples.size * 0.95).toInt().coerceAtMost(samples.lastIndex)
        return BenchmarkResult(benchmark.name, samples[samples.size / 2], samples[p95Index], samples.first())
    }

    private fun consume(call: (Int) -> Int, iterations: Int) {
        var checksum = 0L
        repeat(iterations) { index -> checksum += call(index) }
        blackhole = checksum
    }

    private fun format(value: Double): String = String.format(java.util.Locale.ROOT, "%.3f", value)

    private data class Benchmark(val name: String, val call: (Int) -> Int)

    private data class BenchmarkResult(
        val name: String,
        val medianNs: Double,
        val p95Ns: Double,
        val minNs: Double,
    )
}
