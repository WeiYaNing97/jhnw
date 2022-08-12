package com.sgcc.web.controller.sql;

import org.springframework.stereotype.Component;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年08月12日 14:18
 */
@Component("scheduledTasks")
public class ScheduledTasks {
    public void deleteExpiredData(){
        System.err.println("定时任务启动");
    }
}