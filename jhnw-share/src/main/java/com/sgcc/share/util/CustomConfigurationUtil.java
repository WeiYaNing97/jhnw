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
    /**
     * 读取配置文件内容到常量类
     *
     * @return 无返回值
     * @throws IOException 如果关闭输入流时发生异常
     */
    @ApiOperation("读取配置文件内容到常量类")
    @PostMapping("/ObtainAllConfigurationFileParameters")
    public void ObtainAllConfigurationFileParameters() {
        // 配置文件路径
        String path3 = "/application-config.yml";/*customconfiguration*/
        // 获取配置文件输入流
        InputStream inputStream = this.getClass().getResourceAsStream(path3);
        // 创建Yaml对象
        Yaml yaml = new Yaml();
        // 读取配置文件内容并设置到常量类中
        Constant.setProfileInformation(yaml.load(inputStream));
        try {
            // 关闭输入流
            inputStream.close();
        } catch (IOException e) {
            // 如果关闭输入流时发生异常，则打印异常堆栈信息
            e.printStackTrace();
        }
    }

    /**
     * 根据key从Map中获取value值。
     *
     * @param key 字符串类型的key值
     * @param map 存储键值对的Map对象
     * @return 返回对应key的value值，如果key不存在或value为null，则返回null；
     *         如果key对应的value不是Map类型，则当key存在时返回value值，否则返回null；
     *         如果key中包含"."，则进行多级查询，返回最内层Map中对应key的value值。
     */
    public static Object getValue(String key, Map<String, Object> map) {
        // 将key按照"."进行分割，得到keys数组
        String[] keys = key.split("\\.");

        // 如果keys数组长度为1，表示key中没有"."
        if (keys.length == 1) {
            // 直接从map中获取key对应的value值
            return map.get(keys[0]);
        } else {
            // 提取"."后面的子key
            String subKey = key.substring(keys[0].length() + 1);
            // 获取key对应的value值，保存在subMap变量中
            Object subMap = map.get(keys[0]);
            // 判断subMap是否是Map类型
            if (subMap instanceof Map) {
                // 如果是Map类型，则递归调用getValue方法，继续查询子key对应的value值
                return getValue(subKey, (Map<String, Object>) subMap);
            } else {
                // 如果不是Map类型，则返回null
                return null;
            }
        }
    }
}
