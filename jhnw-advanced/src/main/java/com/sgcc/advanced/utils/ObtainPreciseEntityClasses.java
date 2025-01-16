package com.sgcc.advanced.utils;

import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.WorkThreadMonitor;

import java.util.List;
import java.util.Map;

public class ObtainPreciseEntityClasses<T> {
    private T t;

    public T genericObtainsExactEntityClasses(SwitchParameters switchParameters,
                    List<T> tList) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /**
         * 1：获取第一个交换机错误包命令 作为初始的比较对象
         *  并获取初始的精确度 不等于*则加1
         */
        T t = tList.get(0);
        Map<String, Object> properties_initial = MyUtils.extractProperties(t, "switchType", "firewareVersion", "subVersion");
        //初始对象的精确度 不等于*则加1
        int usedNumber = 0;
        for (Object value : properties_initial.values()) {
            if (!"*".equals(value)) {
                usedNumber++;
            }
        }


        /*遍历交换机问题集合*/
        for (int i=1;i<tList.size();i++){
            /**
             * 2：获取新遍历的对象的精确度 不等于*则加1
             * */
            Map<String, Object> properties_current = MyUtils.extractProperties(tList.get(i), "switchType", "firewareVersion", "subVersion");
            int newNumber = 0;
            for (Object value : properties_current.values()) {
                if (!"*".equals(value)) {
                    newNumber++;
                }
            }


            /**
             * 3： 对比精确度的数量大小
             *     如果新遍历到的对象精确度大于初始精确度则进行替代，
             *     如果新遍历到的对象精确度小于初始精确度则遍历新的，
             *     如果新遍历到的对象精确度等于初始精确度则比较四项基本信息的精确度*/
            if (usedNumber < newNumber){
                //如果新遍历到的对象精确度大于初始精确度则进行替代，则赋值给精确实体类
                t = tList.get(i);
                properties_initial = properties_current;
                usedNumber = newNumber;
                continue;
            }else  if (usedNumber > newNumber) {
                //如果新遍历到的对象精确度小于初始精确度则遍历新的，不做任何操作，继续遍历下一个元素
                continue;
            }else if (usedNumber == newNumber){
                /**
                 * 4：如果精确到项一样则去比较项的值
                 * */
                // 比较两个对象中型号属性的属性值。
                String pojotype = (String) properties_initial.get("switchType");
                String errorRateCommandtype = (String) properties_current.get("switchType");
                /*比较两个属性的精确度
                 * 返回正数是第一个数属性精确
                 * 返回负数 是第二个属性更精确
                 * 返回0 则精确性相等 则进行下一步分析*/
                int typeinteger = ScreeningMethod.compareAccuracy(pojotype, errorRateCommandtype);
                if (typeinteger > 0){
                    continue;
                }else if (typeinteger < 0 ){
                    //如果新遍历到的对象精确度大于初始精确度则进行替代，则赋值给精确实体类
                    t = tList.get(i);
                    properties_initial = properties_current;
                    continue;
                }else if (typeinteger == 0){
                    //比较两个对象中 版本 属性的属性值。
                    String pojofirewareVersion = (String) properties_initial.get("firewareVersion");
                    String errorRateCommandFirewareVersion = (String) properties_current.get("firewareVersion");
                    /*比较两个属性的精确度
                     * 返回正数 是第一个数属性精确
                     * 返回负数 是第二个属性更精确
                     * 返回0 则精确性相等 则进行下一步分析*/
                    int firewareVersioninteger = ScreeningMethod.compareAccuracy(pojofirewareVersion, errorRateCommandFirewareVersion);
                    if (firewareVersioninteger > 0){
                        continue;
                    }else if (firewareVersioninteger < 0){
                        //如果新遍历到的对象精确度大于初始精确度则进行替代，则赋值给精确实体类
                        t = tList.get(i);
                        properties_initial = properties_current;
                        continue;
                    }else if (firewareVersioninteger == 0){
                        //比较两个对象中 子版本 属性的属性值。
                        String pojosubVersion = (String) properties_initial.get("subVersion");
                        String errorRateCommandSubVersion = (String) properties_current.get("subVersion");
                        /*比较两个属性的精确度
                         * 返回正数 是第一个数属性精确
                         * 返回负数 是第二个属性更精确
                         * 返回0 则精确性相等 则进行下一步分析*/
                        int subVersioninteger = ScreeningMethod.compareAccuracy(pojosubVersion, errorRateCommandSubVersion);
                        if (subVersioninteger > 0){
                            continue;
                        }else if (subVersioninteger < 0){
                            //如果新遍历到的对象精确度大于初始精确度则进行替代，则赋值给精确实体类
                            t = tList.get(i);
                            properties_initial = properties_current;
                            continue;
                        }else if (subVersioninteger == 0){
                            /* 如果 都相等 则 四项基本信息完全一致 此时 不应该存在
                             * 因为 sql 有联合唯一索引  四项基本信息+范式名称+范式分类
                             * */
                            continue;
                        }
                    }
                }
            }
        }
        return t;
    }
}
