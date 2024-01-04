package com.sgcc;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.util.Timed;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RuoYiApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  若依启动成功   ლ(´ڡ`ლ)ﾞ  \n" );
        try {
            /*获取配置文件信息*/
            Constant character = new Constant();
            character.ObtainAllConfigurationFileParameters();

            Timed.atRegularTime();

            String projectPath = System.getProperty("user.dir");
            System.err.println("项目所在路径： " + projectPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
