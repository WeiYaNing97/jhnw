package com.sgcc.share.util;
import com.sgcc.share.domain.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.util.Map;

@Api("配置文件相关")
public class CustomConfigurationUtil {
    @ApiOperation("读取配置文件内容到常量类")
    @PostMapping("/ObtainAllConfigurationFileParameters")
    public void ObtainAllConfigurationFileParameters() {
        String path3 = "/customconfiguration.yml";

        InputStream inputStream = this.getClass().getResourceAsStream(path3);

        Yaml yaml = new Yaml();
        Constant.setProfileInformation(yaml.load(inputStream));
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
