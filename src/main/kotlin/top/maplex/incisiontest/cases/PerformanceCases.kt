package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Graft
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Selector
import taboolib.module.incision.annotation.SelectorKind
import taboolib.module.incision.annotation.Site
import taboolib.module.incision.annotation.Splice
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.annotation.Trail
import taboolib.module.incision.api.Anchor
import taboolib.module.incision.api.Theatre

/** 为性能基准安装最小无状态 advice，避免计数器或日志污染被测路径。 */
@Surgeon
object PerformanceCases {

    private const val FIXTURE = "top.maplex.incisiontest.fixture.PerformanceFixture"
    private const val FIXTURE_INTERNAL = "top/maplex/incisiontest/fixture/PerformanceFixture"

    /** 仅供 JMH 启动前验证实际织入；性能方法不得复用该计数器。 */
    @Volatile
    var probeHits = 0

    @Lead(scope = "$FIXTURE#probe(int)int")
    fun probe(theatre: Theatre) {
        probeHits++
    }

    @Lead(scope = "$FIXTURE#lead(int)int")
    fun lead(theatre: Theatre) = Unit

    @Lead(scope = "$FIXTURE#leadTrail(int)int")
    fun leadOfPair(theatre: Theatre) = Unit

    @Trail(scope = "$FIXTURE#leadTrail(int)int")
    fun trailOfPair(theatre: Theatre) = Unit

    @Lead(scope = "$FIXTURE#predicate(int)int", predicate = "args[0] >= 0")
    fun predicate(theatre: Theatre) = Unit

    @Splice(scope = "$FIXTURE#splice(int)int")
    fun splice(theatre: Theatre): Any? = theatre.resume.proceed()

    @Graft(
        scope = "$FIXTURE#siteInvoke(int)int",
        site = Site(
            anchor = Anchor.INVOKE,
            target = Selector(
                kind = SelectorKind.METHOD,
                owner = FIXTURE_INTERNAL,
                name = "helper",
                descriptor = "(I)I",
            ),
        ),
    )
    fun siteInvoke(theatre: Theatre) = Unit
}
