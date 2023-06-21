package com.sgcc.advanced.utils;

import com.sgcc.advanced.domain.ErrorRate;
import com.sgcc.advanced.domain.ErrorRateCommand;
import com.sgcc.advanced.domain.LightAttenuationCommand;
import com.sgcc.advanced.domain.OspfCommand;

import java.util.*;

public class ScreeningMethod {

    /**
     * ErrorRateCommand
     * 从errorRateCommandList中 获取四项基本最详细的数据
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
                     * 返回1 是第一个数属性精确 返回2 是第二个属性更精确
                     * 返回0 则精确性相等 则进行下一步分析*/
                    Integer typeinteger = filterAccurately(pojotype, errorRateCommandtype);
                    if (typeinteger == 1){
                        continue;
                    }else if (typeinteger == 2){
                        errorRateCommandPojo = errorRateCommand;
                        continue;
                    }else if (typeinteger == 0){
                        String pojofirewareVersion = errorRateCommandPojo.getFirewareVersion();
                        String errorRateCommandFirewareVersion = errorRateCommand.getFirewareVersion();
                        /*比较两个属性的精确度*/
                        Integer firewareVersioninteger = filterAccurately(pojofirewareVersion, errorRateCommandFirewareVersion);
                        if (firewareVersioninteger == 1){
                            continue;
                        }else if (firewareVersioninteger == 2){
                            errorRateCommandPojo = errorRateCommand;
                            continue;
                        }else if (firewareVersioninteger == 0){
                            String pojosubVersion = errorRateCommandPojo.getSubVersion();
                            String errorRateCommandSubVersion = errorRateCommand.getSubVersion();
                            /*比较两个属性的精确度*/
                            Integer subVersioninteger = filterAccurately(pojosubVersion, errorRateCommandSubVersion);
                            if (subVersioninteger == 1){
                                continue;
                            }else if (subVersioninteger == 2){
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
     * lightAttenuationCommand
     */
    public static LightAttenuationCommand ObtainPreciseEntityClassesLightAttenuationCommand(List<LightAttenuationCommand> lightAttenuationCommandList) {
        /*定义返回内容*/
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
                     * 返回1 是第一个数属性精确 返回2 是第二个属性更精确
                     * 返回0 则精确性相等 则进行下一步分析*/
                    Integer typeinteger = filterAccurately(pojotype, errorRateCommandtype);
                    if (typeinteger == 1){
                        continue;
                    }else if (typeinteger == 2){
                        lightAttenuationCommandPojo = lightAttenuationCommand;
                        continue;
                    }else if (typeinteger == 0){
                        String pojofirewareVersion = lightAttenuationCommandPojo.getFirewareVersion();
                        String errorRateCommandFirewareVersion = lightAttenuationCommand.getFirewareVersion();
                        /*比较两个属性的精确度*/
                        Integer firewareVersioninteger = filterAccurately(pojofirewareVersion, errorRateCommandFirewareVersion);
                        if (firewareVersioninteger == 1){
                            continue;
                        }else if (firewareVersioninteger == 2){
                            lightAttenuationCommandPojo = lightAttenuationCommand;
                            continue;
                        }else if (firewareVersioninteger == 0){
                            String pojosubVersion = lightAttenuationCommandPojo.getSubVersion();
                            String errorRateCommandSubVersion = lightAttenuationCommand.getSubVersion();
                            /*比较两个属性的精确度*/
                            Integer subVersioninteger = filterAccurately(pojosubVersion, errorRateCommandSubVersion);
                            if (subVersioninteger == 1){
                                continue;
                            }else if (subVersioninteger == 2){
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
     * OspfCommand
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
                     * 返回1 是第一个数属性精确 返回2 是第二个属性更精确
                     * 返回0 则精确性相等 则进行下一步分析*/
                    Integer typeinteger = filterAccurately(pojotype, errorRateCommandtype);
                    if (typeinteger == 1){
                        continue;
                    }else if (typeinteger == 2){
                        ospfCommandPojo = ospfCommand;
                        continue;
                    }else if (typeinteger == 0){
                        String pojofirewareVersion = ospfCommandPojo.getFirewareVersion();
                        String errorRateCommandFirewareVersion = ospfCommand.getFirewareVersion();
                        /*比较两个属性的精确度*/
                        Integer firewareVersioninteger = filterAccurately(pojofirewareVersion, errorRateCommandFirewareVersion);
                        if (firewareVersioninteger == 1){
                            continue;
                        }else if (firewareVersioninteger == 2){
                            ospfCommandPojo = ospfCommand;
                            continue;
                        }else if (firewareVersioninteger == 0){
                            String pojosubVersion = ospfCommandPojo.getSubVersion();
                            String errorRateCommandSubVersion = ospfCommand.getSubVersion();
                            /*比较两个属性的精确度*/
                            Integer subVersioninteger = filterAccurately(pojosubVersion, errorRateCommandSubVersion);
                            if (subVersioninteger == 1){
                                continue;
                            }else if (subVersioninteger == 2){
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
