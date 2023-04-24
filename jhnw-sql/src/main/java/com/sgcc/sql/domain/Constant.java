package com.sgcc.sql.domain;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/*常量类*/
public class Constant {
    /*配置文件信息*/
    private static Map<String, Object> ProfileInformation;
    public void ObtainAllConfigurationFileParameters() {
        String path3 = "/customconfiguration.yml";
        InputStream inputStream = this.getClass().getResourceAsStream(path3);
        Yaml yaml = new Yaml();
        ProfileInformation = yaml.load(inputStream);
        return;
    }

    public static Map<String, Object> getProfileInformation() {
        return ProfileInformation;
    }

    public static void setProfileInformation(Map<String, Object> profileInformation) {
        ProfileInformation = profileInformation;
    }
}
