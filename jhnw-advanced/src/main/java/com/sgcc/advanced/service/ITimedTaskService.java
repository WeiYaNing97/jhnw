package com.sgcc.advanced.service;

import java.util.List;
import com.sgcc.advanced.domain.TimedTask;

/**
 * 定时任务Service接口
 * 
 * @author ruoyi
 * @date 2024-04-07
 */
public interface ITimedTaskService 
{
    /**
     * 查询定时任务
     * 
     * @param id 定时任务主键
     * @return 定时任务
     */
    public TimedTask selectTimedTaskById(Long id);

    /**
     * 查询定时任务列表
     * 
     * @param timedTask 定时任务
     * @return 定时任务集合
     */
    public List<TimedTask> selectTimedTaskList(TimedTask timedTask);

    /**
     * 新增定时任务
     * 
     * @param timedTask 定时任务
     * @return 结果
     */
    public int insertTimedTask(TimedTask timedTask);

    /**
     * 修改定时任务
     * 
     * @param timedTask 定时任务
     * @return 结果
     */
    public int updateTimedTask(TimedTask timedTask);

    /**
     * 批量删除定时任务
     * 
     * @param ids 需要删除的定时任务主键集合
     * @return 结果
     */
    public int deleteTimedTaskByIds(Long[] ids);

    /**
     * 删除定时任务信息
     * 
     * @param id 定时任务主键
     * @return 结果
     */
    public int deleteTimedTaskById(Long id);

    /** 查询所有开启状态定时任务 */
    List<TimedTask> queryAllEnabledTimedTasks();
}
