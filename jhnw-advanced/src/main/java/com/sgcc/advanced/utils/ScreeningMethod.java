package com.sgcc.advanced.utils;

import com.sgcc.advanced.domain.*;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.WorkThreadMonitor;

import java.util.*;

/**
 * 获取四项基本最详细的数据
 */
public class ScreeningMethod {

    /**
     * 日常巡检 光衰命令筛选方法
     * @Description 从lightAttenuationCommandList中获取四项基本最详细的数据
     * @author charles
     * @createTime 2023/12/18 14:09
     * @desc
     *  实现逻辑为:
     * 先比较两个实体类中四项基本信息字段为＊的多少。
     * 如果字段为 * 的个数不相同，则字段为 * 少的实体类更加精确，则赋值给精确实体类。
     * 如果两个实体类字段为 * 的个数相同的时候都比较，不为星的字段哪个更加精确。 例如 S* 与 S2152 ，S2152 更加精确
     *
     * @param lightAttenuationCommandList
     * @return
     */
    public static LightAttenuationCommand ObtainPreciseEntityClassesLightAttenuationCommand(SwitchParameters switchParameters,
                                                                                            List<LightAttenuationCommand> lightAttenuationCommandList) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /**
         * 1：创建一个返回的精确的光衰命令对象,默认取对象集合中的第一个
         */
        LightAttenuationCommand lightAttenuationCommandPojo = lightAttenuationCommandList.get(0);

        /**
         * 2：获取已经选中的光衰命令对象中四项基本信息的精确度,
         *      如果属性值不为*则数值加一，数值代表非*数，即数值越大则更精确。
         */
        int usedNumber = 0;
        if (!(lightAttenuationCommandPojo.getSwitchType().equals("*"))){
            usedNumber = usedNumber +1;
        }
        if (!(lightAttenuationCommandPojo.getFirewareVersion().equals("*"))){
            usedNumber = usedNumber +1;
        }
        if (!(lightAttenuationCommandPojo.getSubVersion().equals("*"))){
            usedNumber = usedNumber +1;
        }

        for (int i = 1; i<lightAttenuationCommandList.size();i++){
            /**
             * 3：获取对象集合中对应下标元素的 四项基本信息的精确度
             *      如果属性值不为*则数值加一，数值代表非*数，即数值越大则更精确。
             */
            int newNumber = 0;
            if (!(lightAttenuationCommandList.get(i).getSwitchType().equals("*"))){
                newNumber = newNumber +1;
            }
            if (!(lightAttenuationCommandList.get(i).getFirewareVersion().equals("*"))){
                newNumber = newNumber +1;
            }
            if (!(lightAttenuationCommandList.get(i).getSubVersion().equals("*"))){
                newNumber = newNumber +1;
            }

            /**
             * 4：对比参数的数量大小
             *       如果属性值不为*则数值加一，数值代表非*数，即数值越大则更精确。
             *
             *       usedNumber < newNumber 遍历到的元素比选中的对象精确，替换对象和对象的精确度
             *       usedNumber > newNumber 选中的对象比遍历到的元素精确，不做任何操作，继续遍历下一个元素
             *       usedNumber == newNumber 两个元素精确到项一样，则去比较三项的值哪一个更加精确。例如型号：S2152 和 S*  选择 S2152
             */
            if (usedNumber < newNumber){
                /* 遍历到的元素 比 选中的对象精确，替换对象和对象的精确度*/
                lightAttenuationCommandPojo = lightAttenuationCommandList.get(i);
                usedNumber = newNumber;
                continue;
            }else  if (usedNumber > newNumber) {
                /* 选中的对象比遍历到的元素精确，不做任何操作，继续遍历下一个元素*/
                continue;
            }else if (usedNumber == newNumber){

                /**
                 * 5：比较型号项的值哪一个更加精确。例如型号：S2152 和 S*  选择 S2152
                 *      实现逻辑为:获取两个对象中型号属性的属性值。
                 *        比较两个属性的精确度
                 *            返回正数 是第一个数属性精确 返回负数 是第二个属性更精确
                 *            返回0 则精确性相等 则进行下一步分析
                 */
                String pojotype = lightAttenuationCommandPojo.getSwitchType();
                String lightAttenuationCommandType = lightAttenuationCommandList.get(i).getSwitchType();
                int typeinteger = compareAccuracy(pojotype, lightAttenuationCommandType);
                if (typeinteger > 0){
                    continue;
                }else if (typeinteger < 0 ){
                    lightAttenuationCommandPojo = lightAttenuationCommandList.get(i);
                    continue;
                }else if (typeinteger == 0){

                    /**
                     * 6：比较版本项的值哪一个更加精确。
                     *      实现逻辑为:获取两个对象中属性的属性值。
                     *        比较两个属性的精确度
                     *            返回正数 是第一个数属性精确 返回负数 是第二个属性更精确
                     *            返回0 则精确性相等 则进行下一步分析
                     */
                    String pojofirewareVersion = lightAttenuationCommandPojo.getFirewareVersion();
                    String lightAttenuationCommandFirewareVersion = lightAttenuationCommandList.get(i).getFirewareVersion();
                    int firewareVersioninteger = compareAccuracy(pojofirewareVersion, lightAttenuationCommandFirewareVersion);
                    if (firewareVersioninteger > 0){
                        continue;
                    }else if (firewareVersioninteger < 0){
                        lightAttenuationCommandPojo = lightAttenuationCommandList.get(i);
                        continue;
                    }else if (firewareVersioninteger == 0){

                        /**
                         * 7：比较子版本项的值哪一个更加精确。
                         *      实现逻辑为:获取两个对象中属性的属性值。
                         *        比较两个属性的精确度
                         *            返回正数 是第一个数属性精确 返回负数 是第二个属性更精确
                         *            返回0 则精确性相等 则进行下一步分析
                         */
                        String pojosubVersion = lightAttenuationCommandPojo.getSubVersion();
                        String lightAttenuationCommandSubVersion = lightAttenuationCommandList.get(i).getSubVersion();
                        int subVersioninteger = compareAccuracy(pojosubVersion, lightAttenuationCommandSubVersion);
                        if (subVersioninteger > 0){
                            continue;
                        }else if (subVersioninteger < 0){
                            lightAttenuationCommandPojo = lightAttenuationCommandList.get(i);
                            continue;
                        }else if (subVersioninteger == 0){

                            /** 如果 都相等 则 四项基本信息完全一致 此时 不应该存在
                             * 因为 sql 有联合唯一索引  四项基本信息+范式名称+范式分类
                             * */
                            continue;
                        }
                    }
                }
            }
        }
        return lightAttenuationCommandPojo;
    }

    /**
    * 日常巡检 错误包命令筛选方法
     * @Description  从errorRateCommandList中 获取四项基本最详细的数据
    * @author charles
    * @createTime 2023/12/18 14:45
    * @desc
     * 实现逻辑为:
     *     先比较两个实体类中四项基本信息字段为＊的多少。
     *     如果字段为 * 的个数不相同，则字段为 * 少的实体类更加精确，则赋值给精确实体类。
     *     如果两个实体类字段为 * 的个数相同的时候都比较，不为星的字段哪个更加精确。 例如 S* 与 S2152 ，S2152 更加精确
    * @param errorRateCommandList
     * @return
    */
    public static ErrorRateCommand ObtainPreciseEntityClassesErrorRateCommand(SwitchParameters switchParameters,
                                                                              List<ErrorRateCommand> errorRateCommandList) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /**
         * 1：获取第一个交换机问题 作为初始的比较对象
         *  并获取初始的精确度 不等于*则加1
         */
        ErrorRateCommand errorRateCommandPojo = errorRateCommandList.get(0);
        int usedNumber = 0;
        if (!(errorRateCommandPojo.getSwitchType().equals("*"))){
            usedNumber = usedNumber +1;
        }
        if (!(errorRateCommandPojo.getFirewareVersion().equals("*"))){
            usedNumber = usedNumber +1;
        }
        if (!(errorRateCommandPojo.getSubVersion().equals("*"))){
            usedNumber = usedNumber +1;
        }


        /*遍历交换机问题集合*/
        for (int i=1;i<errorRateCommandList.size();i++){
            /**
             * 2：获取新遍历的对象的精确度 不等于*则加1
             * */
            int newNumber = 0;
            if (!(errorRateCommandList.get(i).getSwitchType().equals("*"))){
                newNumber = newNumber +1;
            }
            if (!(errorRateCommandList.get(i).getFirewareVersion().equals("*"))){
                newNumber = newNumber +1;
            }
            if (!(errorRateCommandList.get(i).getSubVersion().equals("*"))){
                newNumber = newNumber +1;
            }

            /**
             * 3： 对比精确度的数量大小
             *     如果新遍历到的对象精确度大于初始精确度则进行替代，
             *     如果新遍历到的对象精确度小于初始精确度则遍历新的，
             *     如果新遍历到的对象精确度等于初始精确度则比较四项基本信息的精确度*/
            if (usedNumber < newNumber){
                errorRateCommandPojo = errorRateCommandList.get(i);
                usedNumber = newNumber;
                continue;
            }else  if (usedNumber > newNumber) {
                /* map 中的更加精确  则 进行下一层遍历*/
                continue;
            }else if (usedNumber == newNumber){
                /**
                 * 4：如果精确到项一样则去比较项的值
                 * */
                // 比较两个对象中型号属性的属性值。
                String pojotype = errorRateCommandPojo.getSwitchType();
                String errorRateCommandtype = errorRateCommandList.get(i).getSwitchType();
                /*比较两个属性的精确度
                 * 返回正数是第一个数属性精确
                 * 返回负数 是第二个属性更精确
                 * 返回0 则精确性相等 则进行下一步分析*/
                int typeinteger = compareAccuracy(pojotype, errorRateCommandtype);
                if (typeinteger > 0){
                    continue;
                }else if (typeinteger < 0){
                    errorRateCommandPojo = errorRateCommandList.get(i);
                    continue;
                }else if (typeinteger == 0){
                    //比较两个对象中固件版本属性的属性值。
                    String pojofirewareVersion = errorRateCommandPojo.getFirewareVersion();
                    String errorRateCommandFirewareVersion = errorRateCommandList.get(i).getFirewareVersion();
                    int firewareVersioninteger = compareAccuracy(pojofirewareVersion, errorRateCommandFirewareVersion);
                    if (firewareVersioninteger > 0){
                        continue;
                    }else if (firewareVersioninteger < 0){
                        errorRateCommandPojo = errorRateCommandList.get(i);
                        continue;
                    }else if (firewareVersioninteger == 0){
                        //比较两个对象中子版本属性的属性值。
                        String pojosubVersion = errorRateCommandPojo.getSubVersion();
                        String errorRateCommandSubVersion = errorRateCommandList.get(i).getSubVersion();
                        int subVersioninteger = compareAccuracy(pojosubVersion, errorRateCommandSubVersion);
                        if (subVersioninteger > 0){
                            continue;
                        }else if (subVersioninteger < 0){
                            errorRateCommandPojo = errorRateCommandList.get(i);
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
        return errorRateCommandPojo;
    }



    /**
    * @Description 通过四项基本欸的精确度 筛选最精确的OSPF命令
    * @author charles
    * @createTime 2023/12/18 14:42
    * @desc
     * 实现逻辑为:
     *    先比较两个实体类中四项基本信息字段为＊的多少。
     *    如果字段为 * 的个数不相同，则字段为 * 少的实体类更加精确，则赋值给精确实体类。
     *    如果两个实体类字段为 * 的个数相同的时候都比较，不为星的字段哪个更加精确。 例如 S* 与 S2152 ，S2152 更加精确
     *
    * @param ospfCommandList
     * @return
    */
    public static OspfCommand ObtainPreciseEntityClassesOspfCommand(SwitchParameters switchParameters,List<OspfCommand> ospfCommandList) {

        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /*定义返回内容*/
        OspfCommand ospfCommandPojo = new OspfCommand();
        /*遍历交换机问题集合*/
        for (OspfCommand ospfCommand:ospfCommandList){
            /*如果返回为空 则可以直接存入 map集合*/
            if (ospfCommandPojo.getId() != null){
                /*如果不为空 则需要比较 两个问题那个更加精确  精确的存入Map */
                /* 获取 两个交换机问题的 参数数量的精确度 */
                /*map*/
                int usedNumber = 0;
                if (!(ospfCommandPojo.getSwitchType().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                if (!(ospfCommandPojo.getFirewareVersion().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                if (!(ospfCommandPojo.getSubVersion().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                /*新*/
                int newNumber = 0;
                if (!(ospfCommand.getSwitchType().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(ospfCommand.getFirewareVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(ospfCommand.getSubVersion().equals("*"))){
                    newNumber = newNumber +1;
                }


                /*对比参数的数量大小
                 * 如果新遍历到的问题 数量大于 map 中的问题 则进行替代 否则 则遍历新的*/
                if (usedNumber < newNumber){
                    /* 新 比 map中的精确*/
                    ospfCommandPojo = ospfCommand;
                    continue;
                }else if (usedNumber == newNumber){
                    /*如果精确到项一样 则去比较 项的值 哪一个更加精确 例如型号：S2152 和 S*  选择 S2152*/

                    String pojotype = ospfCommandPojo.getSwitchType();
                    String errorRateCommandtype = ospfCommand.getSwitchType();
                    /*比较两个属性的精确度
                     * 返回正数 是第一个数属性精确 返回负数 是第二个属性更精确
                     * 返回0 则精确性相等 则进行下一步分析*/
                    int typeinteger = compareAccuracy(pojotype, errorRateCommandtype);
                    if (typeinteger > 0){
                        continue;
                    }else if (typeinteger < 0 ){
                        ospfCommandPojo = ospfCommand;
                        continue;
                    }else if (typeinteger == 0){

                        String pojofirewareVersion = ospfCommandPojo.getFirewareVersion();
                        String errorRateCommandFirewareVersion = ospfCommand.getFirewareVersion();
                        /*比较两个属性的精确度
                         * 返回正数 是第一个数属性精确 返回负数 是第二个属性更精确
                         * 返回0 则精确性相等 则进行下一步分析*/
                        int firewareVersioninteger = compareAccuracy(pojofirewareVersion, errorRateCommandFirewareVersion);
                        if (firewareVersioninteger > 0){
                            continue;
                        }else if (firewareVersioninteger < 0){
                            ospfCommandPojo = ospfCommand;
                            continue;
                        }else if (firewareVersioninteger == 0){

                            String pojosubVersion = ospfCommandPojo.getSubVersion();
                            String errorRateCommandSubVersion = ospfCommand.getSubVersion();
                            /*比较两个属性的精确度
                             * 返回正数 是第一个数属性精确 返回负数 是第二个属性更精确
                             * 返回0 则精确性相等 则进行下一步分析*/
                            int subVersioninteger = compareAccuracy(pojosubVersion, errorRateCommandSubVersion);
                            if (subVersioninteger > 0){
                                continue;
                            }else if (subVersioninteger < 0){
                                ospfCommandPojo = ospfCommand;
                                continue;
                            }else if (subVersioninteger == 0){
                                /* 如果 都相等 则 四项基本信息完全一致 此时 不应该存在
                                 * 因为 sql 有联合唯一索引  四项基本信息+范式名称+范式分类
                                 * */
                                continue;
                            }
                        }
                    }
                }else  if (usedNumber > newNumber) {
                    /* map 中的更加精确  则 进行下一层遍历*/
                    continue;
                }

            }else {
                ospfCommandPojo = ospfCommand ;
            }

        }
        return ospfCommandPojo;

    }




    /**
     * 从给定的RouteAggregationCommand列表中获取精确度最高的RouteAggregationCommand对象
     *
     * @param routeAggregationCommandList RouteAggregationCommand对象列表
     * @return 精确度最高的RouteAggregationCommand对象
     */
    public static RouteAggregationCommand ObtainPreciseEntityClassesRouteAggregationCommand(SwitchParameters switchParameters,
                                                                                            List<RouteAggregationCommand> routeAggregationCommandList) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }


        /*定义返回内容*/
        RouteAggregationCommand routeAggregationCommandPojo = new RouteAggregationCommand();
        /*遍历交换机问题集合*/
        for (RouteAggregationCommand routeAggregationCommand:routeAggregationCommandList){
            /*如果返回为空 则可以直接存入 map集合*/
            if (routeAggregationCommandPojo.getId() != null){
                /*如果不为空 则需要比较 两个问题那个更加精确  精确的存入Map */
                /* 获取 两个交换机问题的 参数数量的精确度 */
                /*map*/
                int usedNumber = 0;
                if (!(routeAggregationCommandPojo.getSwitchType().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                if (!(routeAggregationCommandPojo.getFirewareVersion().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                if (!(routeAggregationCommandPojo.getSubVersion().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                /*新*/
                int newNumber = 0;
                if (!(routeAggregationCommand.getSwitchType().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(routeAggregationCommand.getFirewareVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(routeAggregationCommand.getSubVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                /*对比参数的数量大小
                 * 如果新遍历到的问题 数量大于 map 中的问题 则进行替代 否则 则遍历新的*/
                if (usedNumber < newNumber){
                    /* 新 比 map中的精确*/
                    routeAggregationCommandPojo = routeAggregationCommand;
                    continue;
                }else if (usedNumber == newNumber){

                    /*如果精确到项一样 则去比较 项的值 哪一个更加精确 例如型号：S2152 和 S*  选择 S2152*/
                    String pojotype = routeAggregationCommandPojo.getSwitchType();
                    String errorRateCommandtype = routeAggregationCommand.getSwitchType();

                    /*比较两个属性的精确度
                     * 返回正数 是第一个数属性精确 返回负数 是第二个属性更精确
                     * 返回0 则精确性相等 则进行下一步分析*/
                    int typeinteger = compareAccuracy(pojotype, errorRateCommandtype);

                    if (typeinteger > 0){
                        continue;
                    }else if (typeinteger < 0){
                        routeAggregationCommandPojo = routeAggregationCommand;
                        continue;
                    }else if (typeinteger == 0){

                        String pojofirewareVersion = routeAggregationCommandPojo.getFirewareVersion();
                        String errorRateCommandFirewareVersion = routeAggregationCommand.getFirewareVersion();

                        /*比较两个属性的精确度*/
                        int firewareVersioninteger = compareAccuracy(pojofirewareVersion, errorRateCommandFirewareVersion);

                        if (firewareVersioninteger > 0){
                            continue;
                        }else if (firewareVersioninteger < 0){
                            routeAggregationCommandPojo = routeAggregationCommand;
                            continue;
                        }else if (firewareVersioninteger == 0){

                            String pojosubVersion = routeAggregationCommandPojo.getSubVersion();
                            String errorRateCommandSubVersion = routeAggregationCommand.getSubVersion();

                            /*比较两个属性的精确度*/
                            int subVersioninteger = compareAccuracy(pojosubVersion, errorRateCommandSubVersion);

                            if (subVersioninteger > 0){
                                continue;
                            }else if (subVersioninteger < 0){
                                routeAggregationCommandPojo = routeAggregationCommand;
                                continue;
                            }else if (subVersioninteger == 0){
                                /* 如果 都相等 则 四项基本信息完全一致 此时 不应该存在
                                 * 因为 sql 有联合唯一索引  四项基本信息+范式名称+范式分类
                                 * */
                                continue;
                            }
                        }
                    }
                }else  if (usedNumber > newNumber) {
                    /* map 中的更加精确  则 进行下一层遍历*/
                    continue;
                }
            }else {
                routeAggregationCommandPojo = routeAggregationCommand ;
            }
        }



        return routeAggregationCommandPojo;
    }




    /**
    * @Description  比较两个属性的精确度
    * @author charles
    * @createTime 2023/12/18 14:18
    * @desc
     *  比较逻辑为：
     * 查看两个字符串是否含有 * 。
     * 如果都含有 * 则 比较字符串的长度，长度更长的，更加精确。长度相等，则两个字符串的精确度相同。返回0
     * 两个字符串，有一个字符串含有 *，则返回 。 没有星的字符串。
     * 如果两个字符串都没有 * 选择两个字符串的精确度相等。返回 0
     *
    * @param value1
     * @param value2
     * @return
    */
    public static Integer filterAccurately1(String value1,String value2) {
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

            /*两个字符串 中 有一个字符串 含有 *    */
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

    /**
    * @Description 比较两个属性的精确度
    * @author charles
    * @createTime 2023/12/18 14:34
    * @desc
     *  去掉字符串中的非数字字符
     * 使用compareTo()方法比较两个字符串
     * 首先去掉了字符串中的非数字字符，然后使用compareTo()方法比较了两个字符串
     *
    * @param value1
     * @param value2
     * @return
     * 这个方法会根据字符串中的数字进行比较，
     * 如果第一个字符串表示的数字小于第二个字符串表示的数字，则返回负数；
     * 如果相等，则返回0；
     * 如果大于，则返回正数。
    */
    public static int compareAccuracy(String value1,String value2) {
        value1 = value1.replaceAll("\\D", "");
        value2 = value2.replaceAll("\\D", "");
        return value1.compareTo(value2);
    }

    public static LinkBindingCommand ObtainPreciseEntityClassesLinkBindingCommand(SwitchParameters switchParameters,
                                                                                  List<LinkBindingCommand> linkBindingCommandList) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /*定义返回内容*/
        LinkBindingCommand linkBindingCommandPojo = new LinkBindingCommand();
        /*遍历交换机问题集合*/
        for (LinkBindingCommand linkBindingCommand:linkBindingCommandList){
            /*如果返回为空 则可以直接存入 map集合*/
            if (linkBindingCommandPojo.getId() != null){
                /*如果不为空 则需要比较 两个问题那个更加精确  精确的存入Map */
                /* 获取 两个交换机问题的 参数数量的精确度 */
                /*map*/
                int usedNumber = 0;
                if (!(linkBindingCommandPojo.getSwitchType().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                if (!(linkBindingCommandPojo.getFirewareVersion().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                if (!(linkBindingCommandPojo.getSubVersion().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                /*新*/
                int newNumber = 0;
                if (!(linkBindingCommand.getSwitchType().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(linkBindingCommand.getFirewareVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(linkBindingCommand.getSubVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                /*对比参数的数量大小
                 * 如果新遍历到的问题 数量大于 map 中的问题 则进行替代 否则 则遍历新的*/
                if (usedNumber < newNumber){
                    /* 新 比 map中的精确*/
                    linkBindingCommandPojo = linkBindingCommand;
                    continue;
                }else if (usedNumber == newNumber){

                    /*如果精确到项一样 则去比较 项的值 哪一个更加精确 例如型号：S2152 和 S*  选择 S2152*/
                    String pojotype = linkBindingCommandPojo.getSwitchType();
                    String errorRateCommandtype = linkBindingCommand.getSwitchType();

                    /*比较两个属性的精确度
                     * 返回正数 是第一个数属性精确 返回负数 是第二个属性更精确
                     * 返回0 则精确性相等 则进行下一步分析*/
                    int typeinteger = compareAccuracy(pojotype, errorRateCommandtype);

                    if (typeinteger > 0){
                        continue;
                    }else if (typeinteger < 0){
                        linkBindingCommandPojo = linkBindingCommand;
                        continue;
                    }else if (typeinteger == 0){

                        String pojofirewareVersion = linkBindingCommandPojo.getFirewareVersion();
                        String errorRateCommandFirewareVersion = linkBindingCommand.getFirewareVersion();

                        /*比较两个属性的精确度*/
                        int firewareVersioninteger = compareAccuracy(pojofirewareVersion, errorRateCommandFirewareVersion);

                        if (firewareVersioninteger > 0){
                            continue;
                        }else if (firewareVersioninteger < 0){
                            linkBindingCommandPojo = linkBindingCommand;
                            continue;
                        }else if (firewareVersioninteger == 0){

                            String pojosubVersion = linkBindingCommandPojo.getSubVersion();
                            String errorRateCommandSubVersion = linkBindingCommand.getSubVersion();

                            /*比较两个属性的精确度*/
                            int subVersioninteger = compareAccuracy(pojosubVersion, errorRateCommandSubVersion);

                            if (subVersioninteger > 0){
                                continue;
                            }else if (subVersioninteger < 0){
                                linkBindingCommandPojo = linkBindingCommand;
                                continue;
                            }else if (subVersioninteger == 0){
                                /* 如果 都相等 则 四项基本信息完全一致 此时 不应该存在
                                 * 因为 sql 有联合唯一索引  四项基本信息+范式名称+范式分类
                                 * */
                                continue;
                            }
                        }
                    }
                }else  if (usedNumber > newNumber) {
                    /* map 中的更加精确  则 进行下一层遍历*/
                    continue;
                }
            }else {
                linkBindingCommandPojo = linkBindingCommand;
            }
        }
        return linkBindingCommandPojo;
    }
}
