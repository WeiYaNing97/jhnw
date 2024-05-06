package com.sgcc.sql.util;

import com.sgcc.advanced.domain.TimedTask;
import com.sgcc.advanced.service.ITimedTaskService;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.sql.controller.TimedTaskController;
import com.sgcc.sql.domain.TimedTaskVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
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
                pojoVO.setSelectFunctions(Arrays.asList(functionArray));
            }
            timedTaskController.InitiateOpenStateTasks(pojoVO);
        }

    }
}
