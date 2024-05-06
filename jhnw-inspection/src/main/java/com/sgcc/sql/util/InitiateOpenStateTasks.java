package com.sgcc.sql.util;

import com.sgcc.advanced.domain.TimedTask;
import com.sgcc.advanced.service.ITimedTaskService;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.util.MemoryCPU;
import com.sgcc.sql.controller.TimedTaskController;
import com.sgcc.sql.domain.TimedTaskVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @program: jhnw
 * @description: 项目启动，开启所有启动状态定时任务
 * @author:
 * @create: 2024-04-12 09:08
 **/
@Component
public class InitiateOpenStateTasks implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private  ITimedTaskService timedTaskService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        /*你的初始化代码
        System.out.println("执行初始化方法"); */
        timedTaskService = SpringBeanUtil.getBean(ITimedTaskService.class);
        /* 查询所有开启状态定时任务 */
        List<TimedTask> list = timedTaskService.queryAllEnabledTimedTasks();

        TimedTaskController timedTaskController = new TimedTaskController();
        for (TimedTask pojo:list){

            TimedTaskVO pojoVO = new TimedTaskVO();
            BeanUtils.copyProperties(pojo,pojoVO);
            if (pojo.getFunctionArray()!=null){
                String[] functionArray = pojo.getFunctionArray().split(",");
                pojoVO.setFunctionName(Arrays.asList(functionArray));
            }
            timedTaskController.InitiateOpenStateTasks(pojoVO);
        }

    }
}
