package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.MatchMode

import taboolib.module.incision.annotation.SelectorKind

import taboolib.module.incision.annotation.Selector

import taboolib.module.incision.annotation.Pointcut

import taboolib.module.incision.annotation.KotlinTarget
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * @KotlinTarget 四组合 (companionInstance × jvmStaticBridge) FF/FT/TF/TT。
 *
 * 目标：[top.maplex.incisiontest.fixture.KotlinTargetMatrixFixture]
 *
 * | 组合 | companionInstance | jvmStaticBridge | advice 目标特征               |
 * |------|-------------------|-----------------|-------------------------------|
 * | FF   | false             | false           | 普通实例方法（不需 @KotlinTarget）|
 * | FT   | false             | true            | 仅静态桥                       |
 * | TF   | true              | false           | 仅 companion 实例方法           |
 * | TT   | true              | true            | 静态桥 + companion 实例两条入口 |
 */
@Surgeon
object KotlinTargetMatrixCases {

    private const val FIX = "top.maplex.incisiontest.fixture.KotlinTargetMatrixFixture"

    @Volatile var ffHits = 0
    @Volatile var ffAltHits = 0
    @Volatile var ftHits = 0
    @Volatile var ftAltHits = 0
    @Volatile var tfHits = 0
    @Volatile var tfAltHits = 0
    @Volatile var ttStaticPathHits = 0
    @Volatile var ttCompanionPathHits = 0
    @Volatile var ttAltStaticPathHits = 0
    @Volatile var ttAltCompanionPathHits = 0

    /** FF —— 普通实例方法路径，无 @KotlinTarget。 */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "ffEcho", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")]))
    fun onFF(theatre: Theatre) { ffHits++ }

    /** FF #2 —— 第二个 FF advice（不同目标方法） */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "ffAltMethod", descriptor = "(I)I")]))
    fun onFFAlt(theatre: Theatre) { ffAltHits++ }

    /** FT —— 仅 jvmStaticBridge。 */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "ftStaticEcho", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")]))
    @KotlinTarget(jvmStaticBridge = true)
    fun onFT(theatre: Theatre) { ftHits++ }

    /** FT #2 —— 第二个 FT advice */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "ftStaticAlt", descriptor = "(I)I")]))
    @KotlinTarget(jvmStaticBridge = true)
    fun onFTAlt(theatre: Theatre) { ftAltHits++ }

    /** TF —— 仅 companionInstance。 */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "tfCompanionEcho", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")]))
    @KotlinTarget(companionInstance = true)
    fun onTF(theatre: Theatre) { tfHits++ }

    /** TF #2 —— 第二个 TF advice */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "tfCompanionAlt", descriptor = "(I)I")]))
    @KotlinTarget(companionInstance = true)
    fun onTFAlt(theatre: Theatre) { tfAltHits++ }

    /** TT —— 静态桥路径。 */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "ttBothEcho", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")]))
    @KotlinTarget(jvmStaticBridge = true)
    fun onTTStatic(theatre: Theatre) { ttStaticPathHits++ }

    /** TT —— companion 实例路径。 */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "ttBothEcho", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")]))
    @KotlinTarget(companionInstance = true)
    fun onTTCompanion(theatre: Theatre) { ttCompanionPathHits++ }

    /** TT #2 —— 第二个 TT 静态桥 advice */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "ttBothAlt", descriptor = "(I)I")]))
    @KotlinTarget(jvmStaticBridge = true)
    fun onTTAltStatic(theatre: Theatre) { ttAltStaticPathHits++ }

    /** TT #2 —— 第二个 TT companion 实例 advice */
    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = "$FIX", name = "ttBothAlt", descriptor = "(I)I")]))
    @KotlinTarget(companionInstance = true)
    fun onTTAltCompanion(theatre: Theatre) { ttAltCompanionPathHits++ }

    fun reset() {
        ffHits = 0; ffAltHits = 0
        ftHits = 0; ftAltHits = 0
        tfHits = 0; tfAltHits = 0
        ttStaticPathHits = 0; ttCompanionPathHits = 0
        ttAltStaticPathHits = 0; ttAltCompanionPathHits = 0
    }
}
