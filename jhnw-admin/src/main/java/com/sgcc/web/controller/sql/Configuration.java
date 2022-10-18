package com.sgcc.web.controller.sql;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Configuration {

    private static String bootstrap_file = "classpath:configurationFile.yml";

    /** 最小超时时间 */
    public static Long minimumTimeout;
    /** 最大超时时间 */
    public static Long maximumTimeout;

    /** 最大超时时间 */
    public static Long numberOfCycles;

    public static void getConfiguration1(){
        Map<String, String> ymlByFileName = YmlUtils.getYmlByFileName(bootstrap_file,"configuration");
        minimumTimeout = Long.valueOf(ymlByFileName.get("configuration.minimumTimeout")).longValue();
        maximumTimeout = Long.valueOf(ymlByFileName.get("configuration.maximumTimeout")).longValue();
        numberOfCycles = Long.valueOf(ymlByFileName.get("configuration.numberOfCycles")).longValue();
    }

    public static void getConfiguration() {
        Configuration configuration = new Configuration();
        //Map<String, String> ymlMap = configuration.getYmlMap();
        Map<String, String> ymlMap = configuration.readMapFromyml();
        minimumTimeout = Long.valueOf(ymlMap.get("minimumTimeout")).longValue();
        maximumTimeout = Long.valueOf(ymlMap.get("maximumTimeout")).longValue();
        numberOfCycles = Long.valueOf(ymlMap.get("numberOfCycles")).longValue();
    }

    public Map<String,String> getYmlMap() {
        String urlproject = System.getProperty("user.dir");
        String urlyml = urlproject+"\\jhnw-admin\\src\\main\\resources\\configurationFile.yml" ;
        String returnymlSting = readStringFromyml(urlyml);
        String[] yml_split = returnymlSting.split("\r\n");
        Map<String,String> ymlMap = new HashMap<>();
        for (String yml_String:yml_split){
            String[] key_value_split = yml_String.split(":");
            if (key_value_split.length == 2){
                ymlMap.put(key_value_split[0].trim(),key_value_split[1].trim());
            }
        }
        return ymlMap;
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

    public static String readStringFromyml(String txtpath) {
        File file = new File(txtpath);
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            while ((s = br.readLine()) != null) {
               String returnSting = (System.lineSeparator() + s).trim()+"\r\n";
                if (returnSting.indexOf("#") ==-1){
                    result.append(returnSting);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
