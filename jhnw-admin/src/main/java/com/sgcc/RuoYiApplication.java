package com.sgcc;
import com.sgcc.sql.controller.Configuration;
import com.sgcc.sql.controller.MemoryCPU;
import com.sgcc.sql.controller.SwitchScanResultController;
import com.sgcc.sql.domain.Constant;
import com.sgcc.sql.domain.OspfEnum;
import com.sgcc.sql.util.CustomConfigurationUtil;
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


            Configuration.getConfiguration();
            MemoryCPU.initSystemInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
