package top.maplex.incisiontest.fixture

/**
 * 复合指令样本 — 涵盖多种字节码形态，供 InsnPattern / OpcodeSeq 测试使用。
 *
 * 每个方法刻意包含**特征鲜明**的字节码序列，便于按 [taboolib.module.incision.annotation.Op]
 * 精确匹配。注意：以下注释里的指令是 javac/kotlinc 在 JDK 8 目标下的典型产物，实际可能微调。
 */
@Suppress("MemberVisibilityCanBePrivate", "unused", "LiftReturnOrAssignment", "RedundantExplicitType")
class ComplexOpcodeFixture {

    var hits: Int = 0
    var counter: Int = 0
    var nameRef: String = "init"

    /** 算术 — ICONST_*; ISTORE; ILOAD; IADD; IMUL; IRETURN */
    fun arithmetic(input: Int): Int {
        hits++
        val a = 1            // ICONST_1; ISTORE
        val b = 2            // ICONST_2; ISTORE
        val c = 3            // ICONST_3; ISTORE
        val d = 4            // ICONST_4; ISTORE
        val e = 5            // ICONST_5; ISTORE
        return input + a + b + c + d + e   // 多次 ILOAD; IADD
    }

    /** 字符串常量 — LDC */
    fun loadStrings(): String {
        hits++
        val tag = "incision-tag"        // LDC "incision-tag"
        val sep = "::"                  // LDC "::"
        return tag + sep + "marker"     // LDC "marker"; INVOKEVIRTUAL String.plus
    }

    /** 字段读写 — GETFIELD / PUTFIELD */
    fun touchFields(): Int {
        hits++
        counter = counter + 1           // GETFIELD counter; ICONST_1; IADD; PUTFIELD counter
        nameRef = "modified"            // LDC; PUTFIELD nameRef
        return counter                  // GETFIELD counter; IRETURN
    }

    /** 对象构造 + 调用 — NEW; DUP; INVOKESPECIAL <init>; INVOKEVIRTUAL */
    fun buildBuffer(): String {
        hits++
        val sb = StringBuilder()        // NEW StringBuilder; DUP; INVOKESPECIAL <init>
        sb.append("a")                  // INVOKEVIRTUAL append
        sb.append("b")                  // INVOKEVIRTUAL append
        return sb.toString()            // INVOKEVIRTUAL toString; ARETURN
    }

    /** 数组分配 — NEWARRAY / ANEWARRAY / ARRAYLENGTH */
    fun arrays(): Int {
        hits++
        val ints = IntArray(3)          // ICONST_3; NEWARRAY T_INT
        val strs = arrayOfNulls<String>(2)  // ICONST_2; ANEWARRAY String
        return ints.size + strs.size    // ARRAYLENGTH; ARRAYLENGTH; IADD
    }

    /** 类型检查 — CHECKCAST / INSTANCEOF */
    fun typeCheck(any: Any): Int {
        hits++
        if (any is String) {            // INSTANCEOF String; IFEQ
            val s = any as String       // CHECKCAST String
            return s.length
        }
        return -1
    }

    /** 跳转 — IFEQ / GOTO */
    fun branch(flag: Boolean): Int {
        hits++
        if (flag) {                     // ILOAD; IFEQ
            return 100                  // BIPUSH 100; IRETURN
        }
        return 200                      // SIPUSH 200; IRETURN
    }

    /** 静态调用 — INVOKESTATIC */
    fun staticCalls(): Int {
        hits++
        val a = Math.max(1, 2)          // INVOKESTATIC Math.max
        val b = Math.min(3, 4)          // INVOKESTATIC Math.min
        return a + b
    }

    /** 抛/捕 — ATHROW */
    fun throwIt(): String {
        hits++
        try {
            throw RuntimeException("boom")  // NEW; DUP; LDC; INVOKESPECIAL; ATHROW
        } catch (e: RuntimeException) {
            return "caught:${e.message}"
        }
    }

    /** 方法间调用 — INVOKEVIRTUAL helper 多次（用于 ordinal 测试） */
    fun chainInvoke(): Int {
        hits++
        val a = helper(10)
        val b = helper(20)
        val c = helper(30)
        val d = helper(40)
        return a + b + c + d
    }

    fun helper(v: Int): Int {
        return v + 1
    }

    /** 数字字面量 LDC — 用于 cst 数字过滤测试 */
    fun numberLdc(): Long {
        hits++
        val big: Long = 12345678901L       // LDC 12345678901L
        val pi: Double = 3.1415926535      // LDC 3.1415926535
        return big + pi.toLong()
    }

    /** 连续字段写 — 供 PUTFIELD repeat=2/3 测试 */
    fun multiFieldPut(): Int {
        hits++
        counter = 1                        // PUTFIELD counter
        counter = 2                        // PUTFIELD counter
        counter = 3                        // PUTFIELD counter
        counter = 4                        // PUTFIELD counter
        counter = 5                        // PUTFIELD counter
        return counter
    }

    /** 5 步序列样本，显式拆开避免被 kotlinc 常量折叠。 */
    fun densePattern(): Int {
        hits++
        val zero = 0
        val one = 1
        val x = zero + one                 // ICONST_0; ICONST_1; IADD; ISTORE
        return x                           // ILOAD; IRETURN
    }

    /** 多接口调用 — INVOKEINTERFACE */
    fun interfaceCall(list: java.util.List<String>): Int {
        hits++
        list.add("a")                      // INVOKEINTERFACE add
        return list.size                   // INVOKEINTERFACE size
    }

    /** 空体方法 — 仅 RETURN（用于空 steps / 无特征匹配测试） */
    fun emptyBody() {
        // 仅 RETURN
    }
}
