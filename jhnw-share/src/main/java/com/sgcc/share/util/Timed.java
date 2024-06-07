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
        // 创建一个定时器对象
        Timer timer = new Timer();
        // 创建一个MemoryCPU任务对象
        MemoryCPU task = new MemoryCPU();
        // 设置延迟时间为0毫秒
        long delay = 0; // 延迟时间，单位为毫秒
        // 设置执行间隔为3000毫秒
        long period = 3000; // 执行间隔，单位为毫秒
        // 使用定时器对象定时执行MemoryCPU任务对象，初始延迟为delay毫秒，执行间隔为period毫秒
        timer.schedule(task, delay, period);
    }

}
