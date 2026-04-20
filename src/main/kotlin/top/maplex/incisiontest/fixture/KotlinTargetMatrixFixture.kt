package top.maplex.incisiontest.fixture

/**
 * @KotlinTarget 四组合 (companionInstance × jvmStaticBridge) FF/FT/TF/TT 目标基座。
 *
 * - FF: 普通成员方法（既无 @JvmStatic 也无 companion）
 * - FT: 仅 jvmStaticBridge —— 同名 @JvmStatic
 * - TF: 仅 companionInstance —— companion 内非 @JvmStatic 方法
 * - TT: 两者并存 —— companion 内 @JvmStatic 方法
 *
 * 注：FF 是 advice 不写 @KotlinTarget 的常态路径，目标必须是普通实例方法。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class KotlinTargetMatrixFixture {

    @Volatile var ffHits = 0
    @Volatile var ffAltHits = 0

    /** FF —— 普通实例方法，advice 不带 @KotlinTarget。 */
    fun ffEcho(v: String): String {
        ffHits++
        return "ff:$v"
    }

    /** FF #2 —— 不同签名（Int 入参）的普通实例方法 */
    fun ffAltMethod(n: Int): Int {
        ffAltHits++
        return n + 100
    }

    companion object {

        @Volatile var ftHits = 0
        @Volatile var ftAltHits = 0
        @Volatile var tfHits = 0
        @Volatile var tfAltHits = 0
        @Volatile var ttHits = 0
        @Volatile var ttAltHits = 0

        /** FT —— 仅 jvmStaticBridge：使用 @JvmStatic，无 companion 实例方法路径。 */
        @JvmStatic
        fun ftStaticEcho(v: String): String {
            ftHits++
            return "ft:$v"
        }

        /** FT #2 —— 另一个 @JvmStatic 方法（Int 入参） */
        @JvmStatic
        fun ftStaticAlt(n: Int): Int {
            ftAltHits++
            return n * 2
        }

        /** TF —— 仅 companionInstance：companion 内不加 @JvmStatic。 */
        fun tfCompanionEcho(v: String): String {
            tfHits++
            return "tf:$v"
        }

        /** TF #2 —— 另一个 companion 实例方法（Int 入参） */
        fun tfCompanionAlt(n: Int): Int {
            tfAltHits++
            return n + 1
        }

        /** TT —— 两路径并存：@JvmStatic 同时生成静态桥与 companion 实例方法。 */
        @JvmStatic
        fun ttBothEcho(v: String): String {
            ttHits++
            return "tt:$v"
        }

        /** TT #2 —— 另一个并存方法（Int 入参） */
        @JvmStatic
        fun ttBothAlt(n: Int): Int {
            ttAltHits++
            return n - 1
        }
    }
}
