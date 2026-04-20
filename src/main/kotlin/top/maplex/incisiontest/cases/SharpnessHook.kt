package top.maplex.incisiontest.cases

import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.enchantment.ItemEnchantments
import org.apache.commons.lang3.mutable.MutableFloat
import taboolib.module.incision.annotation.Operation
import taboolib.module.incision.annotation.Splice
import taboolib.module.incision.annotation.Surgeon
import taboolib.module.incision.api.Theatre

/**
 * 锋利附魔伤害结算 —— 精确拦截示例。
 *
 * 目标方法（static）：
 *   EnchantmentHelper.modifyDamage(ServerLevel, ItemStack, Entity, DamageSource, float) → float
 *
 * 原版实现：
 *   MutableFloat result = new MutableFloat(damage);
 *   runIterationOnItem(itemStack, (enchantment, level) ->
 *       enchantment.value().modifyDamage(serverLevel, level, itemStack, victim, damageSource, result));
 *   return result.floatValue();
 *
 * 本示例在 @Splice 中**不放行原逻辑**，自己复刻迭代：
 *   - 逐附魔调用真实的 Enchantment.modifyDamage()
 *   - 用 MutableFloat 前后差值拿到每个附魔的真实贡献
 *   - 对锋利做特殊处理（替换公式 / 记录真实加成）
 *   - 其他附魔原样保留
 */

private const val MODIFY_DAMAGE =
    "method:net.minecraft.world.item.enchantment.EnchantmentHelper#modifyDamage(net.minecraft.server.level.ServerLevel,net.minecraft.world.item.ItemStack,net.minecraft.world.entity.Entity,net.minecraft.world.damagesource.DamageSource,float)float"

// ============================================================
//  观察者：拿到锋利的真实伤害贡献（不修改任何值）
// ============================================================
@Surgeon
object SharpnessObserver {

    @Splice(scope = MODIFY_DAMAGE)
    fun observeSharpness(theatre: Theatre): Any? {
        val serverLevel = theatre.arg<ServerLevel>(0) ?: return theatre.resume.proceed()
        val itemStack = theatre.arg<ItemStack>(1) ?: return theatre.resume.proceed()
        val victim = theatre.arg<Entity>(2) ?: return theatre.resume.proceed()
        val damageSource = theatre.arg<DamageSource>(3) ?: return theatre.resume.proceed()
        val baseDamage = theatre.arg<Float>(4) ?: return theatre.resume.proceed()

        val enchantments: ItemEnchantments = itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)
        if (enchantments.isEmpty) return theatre.resume.proceed()

        val result = MutableFloat(baseDamage)
        var sharpnessBonus = 0f

        for (entry in enchantments.entrySet()) {
            @Suppress("UNCHECKED_CAST")
            val holder = entry.key as Holder<Enchantment>
            val level = entry.intValue
            val before = result.toFloat()
            holder.value().modifyDamage(serverLevel, level, itemStack, victim, damageSource, result)
            val after = result.toFloat()
            val bonus = after - before

            if (holder.`is`(Enchantments.SHARPNESS) && bonus != 0f) {
                sharpnessBonus = bonus
                val victimName = victim.name.string
                println("§e[锋利观察] 目标: $victimName | 基础伤害: $baseDamage | 锋利 $level 级真实加成: +$bonus | 结算后: $after")
            }
        }

        return result.toFloat()
    }
}

// ============================================================
//  修改者：替换锋利公式，其他附魔原样保留
// ============================================================
@Surgeon
object SharpnessModifier {

    /**
     * 自定义锋利公式。
     * 原版由数据包定义，实际效果约为 1.0 + 0.5 × (level - 1)。
     * 这里改为 2.0 + 1.5 × (level - 1)，约 2 倍效果。
     */
    var customFormula: (level: Int) -> Float = { level ->
        2.0f + 1.5f * (level - 1)
    }

    @Splice(scope = MODIFY_DAMAGE)
    @Operation(id = "sharpness-modifier", enabled = true)
    fun modifySharpness(theatre: Theatre): Any? {
        val serverLevel = theatre.arg<ServerLevel>(0) ?: return theatre.resume.proceed()
        val itemStack = theatre.arg<ItemStack>(1) ?: return theatre.resume.proceed()
        val victim = theatre.arg<Entity>(2) ?: return theatre.resume.proceed()
        val damageSource = theatre.arg<DamageSource>(3) ?: return theatre.resume.proceed()
        val baseDamage = theatre.arg<Float>(4) ?: return theatre.resume.proceed()

        val enchantments: ItemEnchantments = itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)
        if (enchantments.isEmpty) return theatre.resume.proceed()

        val result = MutableFloat(baseDamage)

        for (entry in enchantments.entrySet()) {
            @Suppress("UNCHECKED_CAST")
            val holder = entry.key as Holder<Enchantment>
            val level = entry.intValue

            if (holder.`is`(Enchantments.SHARPNESS)) {
                // 记录原版真实贡献
                val probe = MutableFloat(baseDamage)
                holder.value().modifyDamage(serverLevel, level, itemStack, victim, damageSource, probe)
                val vanillaBonus = probe.toFloat() - baseDamage

                // 用自定义公式替换
                val customBonus = customFormula(level)
                result.add(customBonus)

                println("§a[锋利修改] 锋利 ${level} 级 | 原版真实加成: +$vanillaBonus | 自定义加成: +$customBonus | 当前累计: ${result.toFloat()}")
            } else {
                // 其他附魔原样调用
                holder.value().modifyDamage(serverLevel, level, itemStack, victim, damageSource, result)
            }
        }

        return result.toFloat()
    }
}
