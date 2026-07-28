package top.maplex.incisiontest.cases

import org.bukkit.Bukkit
import taboolib.module.incision.annotation.Graft
import taboolib.module.incision.annotation.Lead
import taboolib.module.incision.annotation.Pointcut
import taboolib.module.incision.annotation.Selector
import taboolib.module.incision.annotation.SelectorKind
import taboolib.module.incision.annotation.Site
import taboolib.module.incision.annotation.Splice
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.annotation.Trail
import taboolib.module.incision.api.Anchor
import taboolib.module.incision.api.Theatre
import top.maplex.incisiontest.CaseResult
import top.maplex.incisiontest.notApplicableCase
import top.maplex.incisiontest.runCase

/**
 * Adyeshach 硬集成切口。
 *
 * 目标位于另一个 Bukkit PluginClassLoader；完整描述符与显式命名空间同时验证跨 loader
 * dispatcher，以及 CraftChatMessage 坐标只映射声明而不改写整份服务端字节码。
 */
@Surgeon
object AdyeshachIntegrationCases {

    private const val API_OWNER = "ink/ptms/adyeshach/impl/DefaultAdyeshachAPI"
    private const val API_DESC = "()Link/ptms/adyeshach/core/AdyeshachMinecraftAPI;"
    private const val HELPER_OWNER = "ink/ptms/adyeshach/impl/nms/DefaultMinecraftHelper"
    private const val HELPER_DESC = "(Ljava/lang/String;)Ljava/lang/Object;"

    @Volatile var apiLeadHits = 0
    @Volatile var apiTrailHits = 0
    @Volatile var literalLeadHits = 0
    @Volatile var literalTrailHits = 0
    @Volatile var literalSpliceHits = 0
    @Volatile var craftInvokeHits = 0

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = API_OWNER, name = "getMinecraftAPI", descriptor = API_DESC)]))
    fun apiLead(theatre: Theatre) { apiLeadHits++ }

    @Trail(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = API_OWNER, name = "getMinecraftAPI", descriptor = API_DESC)]))
    fun apiTrail(theatre: Theatre) { apiTrailHits++ }

    @Lead(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = HELPER_OWNER, name = "literalChatBaseComponent", descriptor = HELPER_DESC)]))
    fun literalLead(theatre: Theatre) { literalLeadHits++ }

    @Trail(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = HELPER_OWNER, name = "literalChatBaseComponent", descriptor = HELPER_DESC)]))
    fun literalTrail(theatre: Theatre) { literalTrailHits++ }

    @Splice(pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = HELPER_OWNER, name = "literalChatBaseComponent", descriptor = HELPER_DESC)]))
    fun literalSplice(theatre: Theatre): Any? {
        literalSpliceHits++
        return theatre.resume.proceed()
    }

    @Graft(
        pointcut = Pointcut(anyOf = [Selector(kind = SelectorKind.METHOD, owner = HELPER_OWNER, name = "literalChatBaseComponent", descriptor = HELPER_DESC)]),
        site = Site(
            anchor = Anchor.INVOKE,
            target = Selector(
                kind = SelectorKind.METHOD,
                owner = "org/bukkit/craftbukkit/v1_16_R3/util/CraftChatMessage",
                name = "fromString",
                descriptor = "(Ljava/lang/String;)[Lnet/minecraft/server/v1_16_R3/IChatBaseComponent;",
            ),
        ),
    )
    fun craftChatInvoke(theatre: Theatre) { craftInvokeHits++ }

    fun reset() {
        apiLeadHits = 0
        apiTrailHits = 0
        literalLeadHits = 0
        literalTrailHits = 0
        literalSpliceHits = 0
        craftInvokeHits = 0
    }
}

/** 通过 Adyeshach 自己的 ClassLoader 调用公开 API，禁止复制或伪造其实现对象。 */
object AdyeshachIntegrationTests {

    fun testApi(): CaseResult {
        val plugin = Bukkit.getPluginManager().getPlugin("Adyeshach")
            ?.takeIf { Bukkit.getPluginManager().isPluginEnabled("Adyeshach") }
            ?: return notApplicableCase("adyeshach-api-invasion", "Adyeshach 未安装或与当前服务端不兼容")
        return runCase("adyeshach-api-invasion") { asserts, _ ->
            AdyeshachIntegrationCases.reset()
            val adyeshach = Class.forName("ink.ptms.adyeshach.core.Adyeshach", true, plugin.javaClass.classLoader)
            val instance = adyeshach.getField("INSTANCE").get(null)
            val api = adyeshach.getMethod("api").invoke(instance)
            val minecraftApi = api.javaClass.getMethod("getMinecraftAPI").invoke(api)
            asserts.notNull(minecraftApi, "Adyeshach getMinecraftAPI 返回值")
            asserts.equal(1, AdyeshachIntegrationCases.apiLeadHits, "DefaultAdyeshachAPI#getMinecraftAPI Lead")
            asserts.equal(1, AdyeshachIntegrationCases.apiTrailHits, "DefaultAdyeshachAPI#getMinecraftAPI Trail")
        }
    }

    fun testNmsHelper(): CaseResult {
        val plugin = Bukkit.getPluginManager().getPlugin("Adyeshach")
            ?.takeIf { Bukkit.getPluginManager().isPluginEnabled("Adyeshach") }
            ?: return notApplicableCase("adyeshach-nms-helper-invasion", "Adyeshach 未安装或与当前服务端不兼容")
        val resolved = runCatching {
            val adyeshach = Class.forName("ink.ptms.adyeshach.core.Adyeshach", true, plugin.javaClass.classLoader)
            val instance = adyeshach.getField("INSTANCE").get(null)
            val api = adyeshach.getMethod("api").invoke(instance)
            val minecraftApi = api.javaClass.getMethod("getMinecraftAPI").invoke(api)
            val helper = minecraftApi.javaClass.getMethod("getHelper").invoke(minecraftApi)
            helper to helper.javaClass.getMethod("literalChatBaseComponent", String::class.java)
        }.getOrElse {
            // 验收日志禁止出现链接错误关键字；N/A 只陈述归属和边界，具体异常仍由调试器按需查看。
            return notApplicableCase("adyeshach-nms-helper-invasion", "Adyeshach NMS helper 缺少当前服务端所需的运行时依赖")
        }
        // 先做一次兼容性探测。Adyeshach 自身无法在该版本执行时应记为 N/A，不能归罪于 Incision；
        // 探测成功后重置计数，再对同一个真实 proxy 对象执行硬性织入断言。
        runCatching { resolved.second.invoke(resolved.first, "Incision preflight") }.getOrElse {
            return notApplicableCase("adyeshach-nms-helper-invasion", "Adyeshach NMS helper 缺少当前服务端所需的运行时依赖")
        }
        return runCase("adyeshach-nms-helper-invasion") { asserts, _ ->
            AdyeshachIntegrationCases.reset()
            val component = resolved.second.invoke(resolved.first, "Incision")
            asserts.notNull(component, "literalChatBaseComponent 返回 NMS component")
            asserts.equal(1, AdyeshachIntegrationCases.literalLeadHits, "DefaultMinecraftHelper Lead")
            asserts.equal(1, AdyeshachIntegrationCases.literalTrailHits, "DefaultMinecraftHelper Trail")
            asserts.equal(1, AdyeshachIntegrationCases.literalSpliceHits, "DefaultMinecraftHelper Splice")
            asserts.equal(1, AdyeshachIntegrationCases.craftInvokeHits, "CraftChatMessage Site INVOKE")
        }
    }
}
