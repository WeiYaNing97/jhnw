package com.sgcc.sql.util;

import ch.qos.logback.core.util.FileUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class CustomConfigurationUtil {

    private static Yaml yaml = new Yaml();

    /*获取具有配置参数*/
    public String obtainConfigurationFileParameterValues(String key) {
        String value = null;
        //InputStream inputStream = new FileInputStream(new File(getRelativePath("customconfiguration.yml")));
        String path3 = "/customconfiguration.yml";
        InputStream inputStream = this.getClass().getResourceAsStream(path3);
        Map<String, Object> map = yaml.load(inputStream);
        Object objectValue = getValue(key, map);
        if (objectValue instanceof String){
            value = (String) objectValue;
        }
        System.err.println(value);
        return value;
    }
    /*获取具有配置参数 或者 配置参数集合*/
    public Object obtainConfigurationFileParameter(String key) {
        Object object = new Object();
        //InputStream inputStream = new FileInputStream(new File(getRelativePath("customconfiguration.yml")));
        String path3 = "/customconfiguration.yml";
        InputStream inputStream = this.getClass().getResourceAsStream(path3);
        Map<String, Object> map = yaml.load(inputStream);
        object = getValue(key, map);
        return object;
    }


    public static Object getValue(String key, Map<String, Object> map) {
        String[] keys = key.split("\\.");
        if (keys.length == 1) {
            return map.get(keys[0]);
        } else {
            String subKey = key.substring(keys[0].length() + 1);
            Object subMap = map.get(keys[0]);
            if (subMap instanceof Map) {
                return getValue(subKey, (Map<String, Object>) subMap);
            } else {
                return null;
            }
        }
    }

    /**
     * 获取相对路径
     * @param filePath  文件名称
     * @return
     * @throws URISyntaxException
     */
    public static String getRelativePath(String filePath) throws URISyntaxException {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        URL url = classLoader.getResource("");
        File file = new File(url.toURI());
        return file.getAbsolutePath() + "/" + filePath;
    }
}
