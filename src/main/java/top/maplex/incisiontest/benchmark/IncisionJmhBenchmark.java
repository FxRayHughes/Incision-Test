package top.maplex.incisiontest.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import top.maplex.incisiontest.fixture.PerformanceFixture;

import java.util.concurrent.TimeUnit;

/**
 * Incision 稳态调用开销基准。
 *
 * <p>每个入口执行相同的 value + 1 业务逻辑，差异仅来自已经安装的 advice。基准本身不得维护
 * 计数器或输出日志，否则测到的是探针副作用而不是 dispatcher、predicate 与 proceed 的成本。</p>
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class IncisionJmhBenchmark {

    private final PerformanceFixture fixture = new PerformanceFixture();

    @Benchmark
    public int baseline() {
        return fixture.baseline(1);
    }

    @Benchmark
    public int lead() {
        return fixture.lead(1);
    }

    @Benchmark
    public int leadTrail() {
        return fixture.leadTrail(1);
    }

    @Benchmark
    public int predicateTrue() {
        return fixture.predicate(1);
    }

    @Benchmark
    public int spliceProceed() {
        return fixture.splice(1);
    }

    @Benchmark
    public int siteInvoke() {
        return fixture.siteInvoke(1);
    }
}
