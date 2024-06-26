package com.sgcc.sql.timer;

import com.sgcc.share.domain.Constant;
import com.sgcc.share.util.CustomConfigurationUtil;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Timer;

/**
 * @program: jhnw
 * @description:
 * @author:
 * @create: 2024-06-18 15:03
 **/
@Component
@Order(2)
public class SpaceManagementTimer implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 创建Timer对象
        Timer timer = new Timer();

        // 创建ErrorPackageTimed任务对象
        SpaceManagement task = new SpaceManagement();/*交换机登录方式*/

        // 自动空间管理周期（天）
        // 设置定时任务的执行时间间隔
        Integer time = (Integer) CustomConfigurationUtil.getValue("configuration.spaceManagement.autoSpaceManagementCycle", Constant.getProfileInformation());


        // timer.schedule(task, delay, period);
        /*long delay = 0; // 延迟时间，单位为毫秒
        long period = time * 60 * 1000; // 执行间隔，单位为毫秒*/
        timer.schedule(task, 0, time * 24 * 60 * (60 * 1000));
    }
}
