package com.sgcc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RuoYiApplication {
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  若依启动成功   ლ(´ڡ`ლ)ﾞ  \n" );

        try {
            /*获取配置文件信息*/
            /*Constant character = new Constant();
            character.ObtainAllConfigurationFileParameters();*/
            /* CPU 内存 */
            /*Timed*/
            /* 开启定时任务 开启状态*/
            /*InitiateOpenStateTasks*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
