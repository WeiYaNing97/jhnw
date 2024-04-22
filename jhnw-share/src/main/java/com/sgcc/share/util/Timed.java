package com.sgcc.share.util;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import java.util.Timer;

/**
 * @program: jhnw
 * @description: 定时任务
 * @author:
 * @create: 2023-12-27 11:46
 **/
@Component
public class Timed implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        /*你的初始化代码
        System.out.println("执行初始化方法"); */

        Timer timer = new Timer();
        MemoryCPU task = new MemoryCPU();
        long delay = 0; // 延迟时间，单位为毫秒
        long period = 3000; // 执行间隔，单位为毫秒
        timer.schedule(task, delay, period);

    }

}
