package com.sgcc.share.controller;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 读取配置文件   configurationFile.yml   配置信息
 */
public class Configuration {

    private static String bootstrap_file = "classpath:configurationFile.yml";

    /** 最小超时时间 */
    public static Long minimumTimeout;
    /** 最大超时时间 */
    public static Long maximumTimeout;

    /** 最大循环次数*/
    public static Long numberOfCycles;

    /** 标识符 */
    public static String identifier;

    /** 日志路径 */
    public static String logPath;


    public void getConfiguration() {
        Configuration configuration = new Configuration();

        Map<String, String> ymlMap = configuration.readMapFromyml();
        minimumTimeout = Long.valueOf(ymlMap.get("minimumTimeout")).longValue();
        maximumTimeout = Long.valueOf(ymlMap.get("maximumTimeout")).longValue();
        numberOfCycles = Long.valueOf(ymlMap.get("numberOfCycles")).longValue();
        identifier = ymlMap.get("identifier").replaceAll("；",";");
        logPath = ymlMap.get("logPath");
    }


    public Map<String,String> readMapFromyml() {
        //jar包内部用读取来测试
        String path3 = "/configurationFile.yml";
        InputStream inputStream = this.getClass().getResourceAsStream(path3);
        byte[] buffer = new byte[1024];
        int n = 0;
        Map<String,String> ymlMap = new HashMap<>();
        try {
            n = inputStream.read(buffer);
            String returnymlSting = new String(buffer, 0, n);
            String[] yml_split = returnymlSting.split("\r\n");
            for (String yml_String:yml_split){
                String[] key_value_split = yml_String.split(":");
                if (key_value_split.length == 2){
                    ymlMap.put(key_value_split[0].trim(),key_value_split[1].trim());
                }
            }
            inputStream.close();
            return ymlMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ymlMap;
    }


}
