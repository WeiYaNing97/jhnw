package com.sgcc.advanced.utils;

import com.sgcc.advanced.domain.ErrorRate;
import com.sgcc.advanced.domain.ErrorRateCommand;
import com.sgcc.advanced.domain.LightAttenuationCommand;
import com.sgcc.advanced.domain.OspfCommand;

import java.util.*;

/**
 * 获取四项基本最详细的数据
 */
public class ScreeningMethod {

    /**
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
    public static ErrorRateCommand ObtainPreciseEntityClassesErrorRateCommand(List<ErrorRateCommand> errorRateCommandList) {
        /*定义返回内容*/
        ErrorRateCommand errorRateCommandPojo = new ErrorRateCommand();
        /*遍历交换机问题集合*/
        for (ErrorRateCommand errorRateCommand:errorRateCommandList){
            /*如果返回为空 则可以直接存入 map集合*/
            if (errorRateCommandPojo.getId() != null){
                /*如果不为空 则需要比较 两个问题那个更加精确  精确的存入Map */
                /* 获取 两个交换机问题的 参数数量的精确度 */
                /*map*/
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
                /*新*/
                int newNumber = 0;
                if (!(errorRateCommand.getSwitchType().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(errorRateCommand.getFirewareVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(errorRateCommand.getSubVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                /*对比参数的数量大小
                 * 如果新遍历到的问题 数量大于 map 中的问题 则进行替代 否则 则遍历新的*/
                if (usedNumber < newNumber){
                    /* 新 比 map中的精确*/
                    errorRateCommandPojo = errorRateCommand;
                    continue;
                }else if (usedNumber == newNumber){

                    /*如果精确到项一样 则去比较 项的值 哪一个更加精确 例如型号：S2152 和 S*  选择 S2152*/
                    String pojotype = errorRateCommandPojo.getSwitchType();
                    String errorRateCommandtype = errorRateCommand.getSwitchType();

                    /*比较两个属性的精确度
                     * 返回正数 是第一个数属性精确 返回负数 是第二个属性更精确
                     * 返回0 则精确性相等 则进行下一步分析*/
                    int typeinteger = compareAccuracy(pojotype, errorRateCommandtype);

                    if (typeinteger > 0){
                        continue;
                    }else if (typeinteger < 0){
                        errorRateCommandPojo = errorRateCommand;
                        continue;
                    }else if (typeinteger == 0){

                        String pojofirewareVersion = errorRateCommandPojo.getFirewareVersion();
                        String errorRateCommandFirewareVersion = errorRateCommand.getFirewareVersion();

                        /*比较两个属性的精确度*/
                        int firewareVersioninteger = compareAccuracy(pojofirewareVersion, errorRateCommandFirewareVersion);

                        if (firewareVersioninteger > 0){
                            continue;
                        }else if (firewareVersioninteger < 0){
                            errorRateCommandPojo = errorRateCommand;
                            continue;
                        }else if (firewareVersioninteger == 0){

                            String pojosubVersion = errorRateCommandPojo.getSubVersion();
                            String errorRateCommandSubVersion = errorRateCommand.getSubVersion();

                            /*比较两个属性的精确度*/
                            int subVersioninteger = compareAccuracy(pojosubVersion, errorRateCommandSubVersion);

                            if (subVersioninteger > 0){
                                continue;
                            }else if (subVersioninteger < 0){
                                errorRateCommandPojo = errorRateCommand;
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
                errorRateCommandPojo = errorRateCommand ;
            }
        }
        return errorRateCommandPojo;
    }

    /**
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
    public static LightAttenuationCommand ObtainPreciseEntityClassesLightAttenuationCommand(List<LightAttenuationCommand> lightAttenuationCommandList) {
        /*定义返回内容 精确实体类*/
        LightAttenuationCommand lightAttenuationCommandPojo = new LightAttenuationCommand();
        /*遍历交换机问题集合*/
        for (LightAttenuationCommand lightAttenuationCommand:lightAttenuationCommandList){
            /*如果返回为空 则可以直接存入 map集合*/
            if (lightAttenuationCommandPojo.getId() != null){
                /*如果不为空 则需要比较 两个问题那个更加精确  精确的存入Map */
                /* 获取 两个交换机问题的 参数数量的精确度 */
                /*map*/

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

                /*新*/
                int newNumber = 0;
                if (!(lightAttenuationCommand.getSwitchType().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(lightAttenuationCommand.getFirewareVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(lightAttenuationCommand.getSubVersion().equals("*"))){
                    newNumber = newNumber +1;
                }

                /*对比参数的数量大小
                 * 如果新遍历到的问题 数量大于 map 中的问题 则进行替代 否则 则遍历新的*/
                if (usedNumber < newNumber){
                    /* 新 比 map中的精确*/
                    lightAttenuationCommandPojo = lightAttenuationCommand;
                    continue;

                }else if (usedNumber == newNumber){

                    /*如果精确到项一样 则去比较 项的值 哪一个更加精确 例如型号：S2152 和 S*  选择 S2152*/
                    String pojotype = lightAttenuationCommandPojo.getSwitchType();
                    String errorRateCommandtype = lightAttenuationCommand.getSwitchType();

                    /*比较两个属性的精确度
                     * 返回正数 是第一个数属性精确 返回负数 是第二个属性更精确
                     * 返回0 则精确性相等 则进行下一步分析*/
                    int typeinteger = compareAccuracy(pojotype, errorRateCommandtype);
                    if (typeinteger > 0){
                        continue;
                    }else if (typeinteger < 0 ){
                        lightAttenuationCommandPojo = lightAttenuationCommand;
                        continue;
                    }else if (typeinteger == 0){

                        String pojofirewareVersion = lightAttenuationCommandPojo.getFirewareVersion();
                        String errorRateCommandFirewareVersion = lightAttenuationCommand.getFirewareVersion();

                        /*比较两个属性的精确度*/
                        int firewareVersioninteger = compareAccuracy(pojofirewareVersion, errorRateCommandFirewareVersion);
                        if (firewareVersioninteger > 0){
                            continue;
                        }else if (firewareVersioninteger < 0){
                            lightAttenuationCommandPojo = lightAttenuationCommand;
                            continue;
                        }else if (firewareVersioninteger == 0){

                            String pojosubVersion = lightAttenuationCommandPojo.getSubVersion();
                            String errorRateCommandSubVersion = lightAttenuationCommand.getSubVersion();

                            /*比较两个属性的精确度*/
                            int subVersioninteger = compareAccuracy(pojosubVersion, errorRateCommandSubVersion);
                            if (subVersioninteger > 0){
                                continue;
                            }else if (subVersioninteger < 0){
                                lightAttenuationCommandPojo = lightAttenuationCommand;
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
                lightAttenuationCommandPojo = lightAttenuationCommand ;
            }
        }
        return lightAttenuationCommandPojo;

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
    public static OspfCommand ObtainPreciseEntityClassesOspfCommand(List<OspfCommand> ospfCommandList) {
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
}
