package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Operation
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * @Operation(id) 4 种形态 —— 仅围绕 id 字段的取值边界。
 *
 * - normal     : "id-variant-normal"
 * - special    : "id::with/special.chars-001"  （含 `:` `/` `.` `-` 等常见容错符号）
 * - empty      : "" → 应回退到方法名作为 id 后缀
 * - veryLong   : 256 字符长串
 */
@Surgeon
object OperationIdVariantsCases {

    private const val FIX = "top.maplex.incisiontest.fixture.OperationIdVariantsFixture"

    /** 256+ 字符极长 id，纯字面量便于作为注解参数。 */
    const val VERY_LONG_ID: String = "veryLong-abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij"

    @Volatile var normalHits = 0
    @Volatile var specialHits = 0
    @Volatile var emptyHits = 0
    @Volatile var longHits = 0

    @Operation(id = "id-variant-normal")
    @Lead(scope = "method:$FIX#idNormalTarget()int")
    fun adviceNormal(theatre: Theatre) { normalHits++ }

    @Operation(id = "id::with/special.chars-001")
    @Lead(scope = "method:$FIX#idSpecialCharsTarget()int")
    fun adviceSpecial(theatre: Theatre) { specialHits++ }

    /** id="" — 应回退到方法名（`adviceEmptyFallback`）作为后缀。 */
    @Operation(id = "")
    @Lead(scope = "method:$FIX#idEmptyFallbackTarget()int")
    fun adviceEmptyFallback(theatre: Theatre) { emptyHits++ }

    @Operation(id = VERY_LONG_ID)
    @Lead(scope = "method:$FIX#idVeryLongTarget()int")
    fun adviceVeryLong(theatre: Theatre) { longHits++ }

    fun reset() {
        normalHits = 0
        specialHits = 0
        emptyHits = 0
        longHits = 0
    }
}
