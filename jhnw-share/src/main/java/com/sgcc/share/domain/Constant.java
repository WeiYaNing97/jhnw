package com.sgcc.share.domain;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/*常量类*/
@Component
@Order(1)
public class Constant  implements ApplicationListener<ApplicationReadyEvent> {
    /*配置文件信息*/
    private static Map<String, Object> ProfileInformation;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String path3 = "/application-config.yml";
        InputStream inputStream = this.getClass().getResourceAsStream(path3);
        Yaml yaml = new Yaml();
        ProfileInformation = yaml.load(inputStream);

        /*关闭IO流*/
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }



    public void ObtainAllConfigurationFileParameters() {
        String path3 = "/application-config.yml";
        InputStream inputStream = this.getClass().getResourceAsStream(path3);
        Yaml yaml = new Yaml();
        ProfileInformation = yaml.load(inputStream);

        /*关闭IO流*/
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }

    public static Map<String, Object> getProfileInformation() {
        return ProfileInformation;
    }

    public static void setProfileInformation(Map<String, Object> profileInformation) {
        ProfileInformation = profileInformation;
    }
}
