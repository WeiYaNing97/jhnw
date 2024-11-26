package com.sgcc.share.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkThreadMonitor {

    // 线程关闭标志  每个任务分配一个唯一的标识符，并使用一个线程安全的集合来管理这些标识符
    private static ConcurrentHashMap<String, AtomicBoolean> shutdownFlags = new ConcurrentHashMap<>();

    /**
     * 获取关闭标志位的值。
     *
     * @param key 关键字，用于从shutdownFlags中获取对应的AtomicBoolean对象
     * @return 如果shutdownFlags中存在以key为键的AtomicBoolean对象，并且其值为true，则返回true；否则返回false
     */
    public static boolean getShutdownFlag(String key) {
        // 从shutdownFlags中获取以key为键的AtomicBoolean对象
        AtomicBoolean shutdownFlag = shutdownFlags.get(key);
        // 返回shutdownFlag不为null且其值为true的情况
        return shutdownFlag != null && shutdownFlag.get();
    }

    /**
     * 设置中断标志位的值。
     *
     * @param key 关键字，用于在shutdownFlags中创建或更新对应的AtomicBoolean对象
     * @param value 关闭标志位的值，true表示关闭，false表示不关闭
     */
    public static void setShutdownFlag(String key, boolean value) {
        // 在shutdownFlags中创建或更新以key为键的AtomicBoolean对象，并将其初始化为value
        shutdownFlags.put(key, new AtomicBoolean(value));
    }

    /**
     * 根据给定的键移除对应的线程关闭标志位。
     *
     * @param key 要移除的键，该键对应于一个线程的关闭标志位
     *
     * 该方法用于从shutdownFlags映射中移除与给定键关联的AtomicBoolean实例。
     * 如果shutdownFlags中存在与给定键关联的AtomicBoolean实例，则将其移除并返回该实例；
     * 如果不存在，则返回null。
     *
     * 注意：此方法直接移除键值对，不进行预期值的匹配检查。
     */
    public static void removeThread(String key) {
        // 只有当 "someKey" 对应的 AtomicBoolean 实例是 expectedValue 时才移除
        // AtomicBoolean expectedValue = new AtomicBoolean(true); // 预期值
        // shutdownFlags.remove(key, expectedValue);

        // 使用 remove 方法移除键值对
        AtomicBoolean remove = shutdownFlags.remove(key);
    }

    /**
     * 根据给定的键和初始值移除对应的线程关闭标志位。
     *
     * @param key           要移除的键，该键对应于一个线程的关闭标志位
     * @param initialValue AtomicBoolean 实例的初始值，用于与预期值进行匹配
     *
     * 该方法尝试从shutdownFlags映射中移除与给定键关联的AtomicBoolean实例。
     * 只有当与键关联的AtomicBoolean实例的值等于initialValue时，该实例才会被移除。
     * 在这个过程中，initialValue作为预期值来与AtomicBoolean实例的当前值进行比较。
     * 如果匹配成功，则移除该AtomicBoolean实例；否则，不移除。
     */
    public static void removeThreadByInitialValue(String key,boolean initialValue) {
        // 只有当 "someKey" 对应的 AtomicBoolean 实例是 expectedValue 时才移除
        AtomicBoolean expectedValue = new AtomicBoolean(initialValue); // 预期值
        shutdownFlags.remove(key, expectedValue);
    }


}
