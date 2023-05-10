package com.sgcc.sql.util;

import com.sgcc.sql.domain.TotalQuestionTable;

import java.util.*;

public class InspectionMethods {
    /**
     * 筛选匹配度高的交换机问题
     * 逻辑 定义一个 map集合 key为 范式分类和范式名称 保证问题的唯一
     * 遍历交换机问题集合 提取范式分类和范式名称  到 map中查询 是否返回实体类
     * 如果返回为空 则可以直接存入 map集合
     * 如果不为空 则需要比较 两个问题那个更加精确  精确的存入Map
     *
     * 如果精确到项一样 则去比较 项的值 哪一个更加精确 例如型号：S2152 和 S*  选择 S2152
     * @param totalQuestionTableList
     *
     * @return
     */
    public static List<TotalQuestionTable> ObtainPreciseEntityClasses(List<TotalQuestionTable> totalQuestionTableList) {
        /*定义返回内容*/
        List<TotalQuestionTable> TotalQuestionTablePojoList = new ArrayList<>();
        /*逻辑 定义一个 map集合 key为 范式分类和范式名称 保证问题的唯一*/
        Map<String,TotalQuestionTable> totalQuestionTableHashMap = new HashMap<>();
        /*遍历交换机问题集合*/
        for (TotalQuestionTable totalQuestionTable:totalQuestionTableList){
            /*提取范式分类和范式名称  到 map中查询 是否返回实体类*/
            String key =totalQuestionTable.getTypeProblem() + totalQuestionTable.getTemProName();
            TotalQuestionTable pojo = totalQuestionTableHashMap.get(key);
            /*如果返回为空 则可以直接存入 map集合*/
            if (pojo != null){
                /*如果不为空 则需要比较 两个问题那个更加精确  精确的存入Map */

                /* 获取 两个交换机问题的 参数数量的精确度 */
                /*map*/
                int usedNumber = 0;
                if (!(pojo.getType().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                if (!(pojo.getFirewareVersion().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                if (!(pojo.getSubVersion().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                /*新*/
                int newNumber = 0;
                if (!(totalQuestionTable.getType().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(totalQuestionTable.getFirewareVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(totalQuestionTable.getSubVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                /*对比参数的数量大小
                 * 如果新遍历到的问题 数量大于 map 中的问题 则进行替代 否则 则遍历新的*/
                if (usedNumber < newNumber){
                    /* 新 比 map中的精确*/
                    totalQuestionTableHashMap.put(key,totalQuestionTable);
                }else if (usedNumber == newNumber){
                    /*如果精确到项一样 则去比较 项的值 哪一个更加精确 例如型号：S2152 和 S*  选择 S2152*/

                    String pojotype = pojo.getType();
                    String totalQuestionTabletype = totalQuestionTable.getType();
                    /*比较两个属性的精确度
                     * 返回1 是第一个数属性精确 返回2 是第二个属性更精确
                     * 返回0 则精确性相等 则进行下一步分析*/
                    Integer typeinteger = filterAccurately(pojotype, totalQuestionTabletype);
                    if (typeinteger == 1){
                        totalQuestionTableHashMap.put(key,pojo);
                    }else if (typeinteger == 2){
                        totalQuestionTableHashMap.put(key,totalQuestionTable);
                    }else if (typeinteger == 0){
                        String pojofirewareVersion = pojo.getFirewareVersion();
                        String totalQuestionTablefirewareVersion = totalQuestionTable.getFirewareVersion();
                        /*比较两个属性的精确度*/
                        Integer firewareVersioninteger = filterAccurately(pojofirewareVersion, totalQuestionTablefirewareVersion);
                        if (firewareVersioninteger == 1){
                            totalQuestionTableHashMap.put(key,pojo);
                        }else if (firewareVersioninteger == 2){
                            totalQuestionTableHashMap.put(key,totalQuestionTable);
                        }else if (firewareVersioninteger == 0){
                            String pojosubVersion = pojo.getSubVersion();
                            String totalQuestionTablesubVersion = totalQuestionTable.getSubVersion();
                            /*比较两个属性的精确度*/
                            Integer subVersioninteger = filterAccurately(pojosubVersion, totalQuestionTablesubVersion);
                            if (subVersioninteger == 1){
                                totalQuestionTableHashMap.put(key,pojo);
                            }else if (subVersioninteger == 2){
                                totalQuestionTableHashMap.put(key,totalQuestionTable);
                            }else if (subVersioninteger == 0){
                                /* 如果 都相等 则 四项基本信息完全一致 此时 不应该存在
                                 * 因为 sql 有联合唯一索引  四项基本信息+范式名称+范式分类
                                 * */
                                continue;
                                //totalQuestionTableHashMap.put(key,totalQuestionTable);
                            }
                        }
                    }
                }else  if (usedNumber > newNumber) {
                    /* map 中的更加精确  则 进行下一层遍历*/
                    continue;
                    //totalQuestionTableHashMap.put(key,pojo);
                }
            }else {
                totalQuestionTableHashMap.put(key,totalQuestionTable);
            }
        }

        /*获取 map 的value值 并更存储到集合中 返回*/
        Iterator<Map.Entry< String, TotalQuestionTable >> iterator = totalQuestionTableHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry< String, TotalQuestionTable > entry = iterator.next();
            TotalQuestionTablePojoList.add(entry.getValue());
        }
        return TotalQuestionTablePojoList;

    }

    /**
     * 比较两个属性的精确度
     * @param value1
     * @param value2
     * @return
     */
    public static Integer filterAccurately(String value1,String value2) {
        /*查看是否包含 * */
        boolean value1Boolean = value1.indexOf("*")!=-1;
        boolean value2Boolean = value2.indexOf("*")!=-1;
        /*两项的长度*/
        int value1Length = value1.length();
        int value2Length = value2.length();

        if (value1Boolean && value2Boolean){
            /*如果两个都含有 * 取最长的*/
            if (value1Length<value2Length){
                return 2;
            }else if (value1Length>value2Length){
                return 1;
            }else if (value1Length == value2Length){
                return 0;
            }
        }else {
            /*两个 至少有一个没含有 * */
            if (value1Boolean || value2Boolean){
                /*有一个含有 * 返回 没有*的*/
                if (value1Boolean){
                    return 2;
                }
                if (value2Boolean){
                    return 1;
                }
            }
        }
        return 0;
    }
}
