package com.sgcc.share.domain;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/*常量类*/
public class Constant {
    /*配置文件信息*/
    private static Map<String, Object> ProfileInformation;
    public void ObtainAllConfigurationFileParameters() {
        String projectPath = System.getProperty("user.dir");
        String path3 = projectPath+"/customconfiguration.yml";
        //InputStream inputStream = this.getClass().getResourceAsStream(path3);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(path3));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
