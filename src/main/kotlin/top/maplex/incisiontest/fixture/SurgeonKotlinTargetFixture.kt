package top.maplex.incisiontest.fixture

/**
 * [top.maplex.incisiontest.cases.SurgeonKotlinTargetCases] 专属 fixture。
 *
 * 覆盖 @KotlinTarget 的两条路径：
 * - companionInstance = true  → companion 实例方法（无 @JvmStatic 桥）
 * - jvmStaticBridge   = true  → 外部类静态桥（@JvmStatic 生成的 static）
 * - 同时开启          → static 调用 与 companion 实例调用 两条入口都被织入
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class SurgeonKotlinTargetFixture {

    companion object {

        // ---- 仅 companion 实例：不加 @JvmStatic ----
        @Volatile var companionOnlyHits: Int = 0
        fun companionOnlyEcho(v: String): String {
            companionOnlyHits++
            return "co-only:$v"
        }

        // ---- 仅 jvmStatic 桥：使用 @JvmStatic ----
        @Volatile var staticOnlyHits: Int = 0
        @JvmStatic
        fun staticOnlyEcho(v: String): String {
            staticOnlyHits++
            return "static-only:$v"
        }

        // ---- 同时开启 companion + jvmStatic ----
        // @JvmStatic 会同时生成：外部类静态桥 + companion 实例方法。
        // 静态桥的实现仅是 INVOKEVIRTUAL 转发到 companion 实例方法。
        @Volatile var bothCallHits: Int = 0
        @JvmStatic
        fun bothEcho(v: String): String {
            bothCallHits++
            return "both:$v"
        }
    }
}
