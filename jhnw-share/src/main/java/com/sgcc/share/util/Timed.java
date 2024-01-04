package com.sgcc.share.util;

import java.util.Timer;

/**
 * @program: jhnw
 * @description: 定时任务
 * @author:
 * @create: 2023-12-27 11:46
 **/
public class Timed {
    /**/
    public static void atRegularTime() {
        Timer timer = new Timer();
        MemoryCPU task = new MemoryCPU();
        long delay = 0; // 延迟时间，单位为毫秒
        long period = 3000; // 执行间隔，单位为毫秒
        timer.schedule(task, delay, period);
    }
}
