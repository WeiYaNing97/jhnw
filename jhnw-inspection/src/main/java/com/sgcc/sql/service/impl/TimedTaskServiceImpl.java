package com.sgcc.sql.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.TimedTaskMapper;
import com.sgcc.sql.domain.TimedTask;
import com.sgcc.sql.service.ITimedTaskService;

/**
 * 定时任务Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-04-07
 */
@Service
public class TimedTaskServiceImpl implements ITimedTaskService 
{
    @Autowired
    private TimedTaskMapper timedTaskMapper;

    /**
     * 查询定时任务
     * 
     * @param id 定时任务主键
     * @return 定时任务
     */
    @Override
    public TimedTask selectTimedTaskById(Long id)
    {
        return timedTaskMapper.selectTimedTaskById(id);
    }

    /**
     * 查询定时任务列表
     * 
     * @param timedTask 定时任务
     * @return 定时任务
     */
    @Override
    public List<TimedTask> selectTimedTaskList(TimedTask timedTask)
    {
        if (timedTask.getTimedTaskStartTime()!=null && (timedTask.getTimedTaskStartTime()+"").indexOf("00:00:00")!=-1){
            String endTime = (timedTask.getTimedTaskStartTime()+"").replace("00:00:00","23:59:59");
            DateFormat gmt = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            Date dateTime = null;
            try {
                dateTime = gmt.parse(endTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return timedTaskMapper.selectTimedTaskListByDataTime(timedTask,dateTime);
        }
        return timedTaskMapper.selectTimedTaskList(timedTask);
    }

    /**
     * 新增定时任务
     * @param timedTask 定时任务
     * @return 结果
     */
    @Override
    public int insertTimedTask(TimedTask timedTask)
    {
        return timedTaskMapper.insertTimedTask(timedTask);
    }

    /**
     * 修改定时任务
     * 
     * @param timedTask 定时任务
     * @return 结果
     */
    @Override
    public int updateTimedTask(TimedTask timedTask)
    {
        return timedTaskMapper.updateTimedTask(timedTask);
    }

    /**
     * 批量删除定时任务
     * 
     * @param ids 需要删除的定时任务主键
     * @return 结果
     */
    @Override
    public int deleteTimedTaskByIds(Long[] ids)
    {
        return timedTaskMapper.deleteTimedTaskByIds(ids);
    }

    /**
     * 删除定时任务信息
     * 
     * @param id 定时任务主键
     * @return 结果
     */
    @Override
    public int deleteTimedTaskById(Long id)
    {
        return timedTaskMapper.deleteTimedTaskById(id);
    }

    /** 查询所有开启状态定时任务 */
    @Override
    public List<TimedTask> queryAllEnabledTimedTasks() {
        return timedTaskMapper.queryAllEnabledTimedTasks();
    }

}
