package top.maplex.incisiontest.cases

import taboolib.module.incision.annotation.Splice
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre
import top.maplex.incisiontest.CaseResult
import top.maplex.incisiontest.runCase
import java.lang.reflect.InvocationTargetException

/**
 * 复现 API v2 重构后的 JVMTI PENDING_LOAD 回归。
 *
 * 本类不能静态引用任何 NMS 类型：Surgeon 注册时目标类必须保持未加载，随后测试用例再通过
 * 反射首次加载并调用 EnchantmentMenu.slotsChanged，才能验证 pending transformer 最终确实生效。
 */
@Surgeon
object PendingLoadNmsCases {

    @Volatile
    private var slotsChangedHits = 0

    /** 该字符串目标对应的类直到测试方法 Class.forName 前都不得加载。 */
    @Splice(scope = "method:top.maplex.incisiontest.fixture.PendingLoadTarget#value()java.lang.String")
    fun interceptPendingFixture(theatre: Theatre): Any? = "woven-pending"

    /** 保留用户实际使用的单行 Scope，防止生命周期修复只在 Pointcut 路径生效。 */
    @Splice(scope = "method:net.minecraft.world.inventory.EnchantmentMenu#slotsChanged(net.minecraft.world.Container)void")
    fun interceptSlotsChanged(theatre: Theatre): Any? {
        slotsChangedHits++
        // 测试实例由 Unsafe 分配，不能执行依赖完整玩家与世界状态的原方法。
        return Unit
    }

    fun testPendingLoadNms(): CaseResult = runCase("backend-jvmti-pending-nms") { asserts, _ ->
        slotsChangedHits = 0
        val loader = Thread.currentThread().contextClassLoader
        val menuClass = Class.forName("net.minecraft.world.inventory.EnchantmentMenu", true, loader)
        val containerClass = Class.forName("net.minecraft.world.Container", true, loader)
        val simpleContainerClass = Class.forName("net.minecraft.world.SimpleContainer", true, loader)
        val menu = allocateWithoutConstructor(menuClass)
        val container = simpleContainerClass.getConstructor(Int::class.javaPrimitiveType).newInstance(2)

        try {
            menuClass.getMethod("slotsChanged", containerClass).invoke(menu, container)
        } catch (error: InvocationTargetException) {
            throw error.targetException
        }

        asserts.equal(1, slotsChangedHits, "PENDING_LOAD 的 EnchantmentMenu 应在首次加载后完成织入")
        asserts.note("Leaf 26.2 EnchantmentMenu.slotsChanged 命中次数=$slotsChangedHits")
    }

    fun testPendingLoadFixture(): CaseResult = runCase("backend-jvmti-pending-fixture") { asserts, _ ->
        val loader = PendingLoadNmsCases::class.java.classLoader
        val target = Class.forName("top.maplex.incisiontest.fixture.PendingLoadTarget", true, loader)
        val value = target.getMethod("value").invoke(null)
        asserts.equal("woven-pending", value, "未来首次加载的 fixture 必须经过 JVMTI ClassFileLoadHook")
    }

    /**
     * 仅用于制造不依赖在线玩家的宿主对象；Splice 必须在原方法执行前返回，否则该未初始化实例会失败。
     */
    private fun allocateWithoutConstructor(type: Class<*>): Any {
        val unsafeClass = Class.forName("sun.misc.Unsafe")
        val field = unsafeClass.getDeclaredField("theUnsafe").apply { isAccessible = true }
        val unsafe = field.get(null)
        return unsafeClass.getMethod("allocateInstance", Class::class.java).invoke(unsafe, type)
    }
}
