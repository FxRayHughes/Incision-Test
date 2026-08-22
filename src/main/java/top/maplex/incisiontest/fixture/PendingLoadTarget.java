package top.maplex.incisiontest.fixture;

/**
 * 只允许由 pending-load 集成用例通过反射首次加载。
 * 生产测试代码不得静态引用本类，否则会把 PENDING_LOAD 场景退化成已加载类重转换。
 */
public final class PendingLoadTarget {

    private PendingLoadTarget() {
    }

    public static String value() {
        return "original";
    }
}
