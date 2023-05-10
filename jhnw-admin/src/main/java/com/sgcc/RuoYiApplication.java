package com.sgcc;
import com.sgcc.share.controller.Configuration;
import com.sgcc.share.util.MemoryCPU;
import com.sgcc.share.domain.Constant;
import com.sgcc.sql.domain.OspfEnum;
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
            Constant character = new Constant();
            character.ObtainAllConfigurationFileParameters();
            OspfEnum.assignment();

            Configuration configuration = new Configuration();
            configuration.getConfiguration();
            MemoryCPU memoryCPU = new MemoryCPU();
            memoryCPU.initSystemInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
