package com.sgcc.advanced.mapper;

import java.util.Date;
import java.util.List;
import com.sgcc.advanced.domain.TimedTask;
import org.apache.ibatis.annotations.Param;

/**
 * 定时任务Mapper接口
 * 
 * @author ruoyi
 * @date 2024-04-07
 */
public interface TimedTaskMapper 
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

    List<TimedTask> selectTimedTaskListByDataTime(@Param("timedTask") TimedTask timedTask,@Param("dateTime") Date dateTime);

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
     * 删除定时任务
     * 
     * @param id 定时任务主键
     * @return 结果
     */
    public int deleteTimedTaskById(Long id);

    /**
     * 批量删除定时任务
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTimedTaskByIds(Long[] ids);

    /** 查询所有开启状态定时任务 */
    List<TimedTask> queryAllEnabledTimedTasks();
}
