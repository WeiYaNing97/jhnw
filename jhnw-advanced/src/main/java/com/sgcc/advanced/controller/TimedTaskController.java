package com.sgcc.advanced.controller;

import java.util.*;
import com.sgcc.advanced.domain.TimedTaskVO;
import com.sgcc.advanced.snapshot.TemplateScheduledTasks;
import com.sgcc.advanced.thread.TimedTaskRetrievalFile;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.domain.entity.SysUser;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.util.MyUtils;
import com.sgcc.system.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.advanced.domain.TimedTask;
import com.sgcc.advanced.service.ITimedTaskService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 定时任务Controller
 *
 * @author ruoyi
 * @date 2024-04-03
 */
@RestController
@RequestMapping("/advanced/TimedTask")
public class TimedTaskController extends BaseController
{
    @Autowired
    private ITimedTaskService timedTaskService;
    @Autowired
    private ISysUserService sysUserService;

    public static HashMap<Long,Timer> timerStorage = new HashMap<>();

    /**
     * 查询定时任务列表
     */
    @PreAuthorize("@ss.hasPermi('advanced:TimedTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(TimedTaskVO timedTaskVO) {
        TimedTask timedTask = new TimedTask();
        BeanUtils.copyProperties(timedTaskVO , timedTask);
        if (timedTaskVO.getFunction() != null){
            timedTask.setFunctionArray( String.join(",", timedTaskVO.getFunction()));
        }

        startPage();
        List<TimedTask> list = timedTaskService.selectTimedTaskList(timedTask);

        List<TimedTaskVO> timedTaskVOS = new ArrayList<>();
        for (TimedTask pojo:list){
            TimedTaskVO pojoVO = new TimedTaskVO();
            BeanUtils.copyProperties(pojo,pojoVO);

            if (pojo.getFunctionArray()!=null){
                String[] functionArray = pojo.getFunctionArray().split(",");
                pojoVO.setFunction(Arrays.asList(functionArray));
            }

            timedTaskVOS.add(pojoVO);
        }

        return getDataTable(timedTaskVOS);
    }

    /**
     * 导出定时任务列表
     */
    @PreAuthorize("@ss.hasPermi('advanced:TimedTask:export')")
    @MyLog(title = "定时任务", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(TimedTask timedTask)
    {
        List<TimedTask> list = timedTaskService.selectTimedTaskList(timedTask);
        ExcelUtil<TimedTask> util = new ExcelUtil<TimedTask>(TimedTask.class);
        return util.exportExcel(list, "定时任务数据");
    }

    /** 获取定时任务详细信息 */
    @PreAuthorize("@ss.hasPermi('advanced:TimedTask:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {   TimedTask timedTask = timedTaskService.selectTimedTaskById(id);

        TimedTaskVO timedTaskVO = new TimedTaskVO();
        BeanUtils.copyProperties(timedTask,timedTaskVO);

        if (timedTask.getFunctionArray()!=null){
            String[] functionArray = timedTask.getFunctionArray().split(",");
            timedTaskVO.setFunction(Arrays.asList(functionArray));
        }

        return AjaxResult.success(timedTaskVO);
    }

    /**
     * 新增定时任务
     */
    @PreAuthorize("@ss.hasPermi('advanced:TimedTask:add')")
    @MyLog(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TimedTaskVO timedTaskVO)
    {
        TimedTask timedTask = new TimedTask();
        BeanUtils.copyProperties(timedTaskVO , timedTask);
        if (timedTaskVO.getFunction() != null){
            timedTask.setFunctionArray( String.join(",", timedTaskVO.getFunction()));
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        timedTask.setCreatorName(loginUser.getUsername());
        return toAjax(timedTaskService.insertTimedTask(timedTask));
    }

    /**
     * 修改定时任务
     */
    @PreAuthorize("@ss.hasPermi('advanced:TimedTask:edit')")
    @MyLog(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TimedTaskVO timedTaskVO)
    {
        TimedTask timedTask = new TimedTask();
        BeanUtils.copyProperties(timedTaskVO , timedTask);
        if (timedTaskVO.getFunction() != null){
            timedTask.setFunctionArray( String.join(",", timedTaskVO.getFunction()));
        }

        return toAjax(timedTaskService.updateTimedTask(timedTask));
    }

    /**
     * 删除定时任务
     */
    @PreAuthorize("@ss.hasPermi('advanced:TimedTask:remove')")
    @MyLog(title = "定时任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){

        return toAjax(timedTaskService.deleteTimedTaskByIds(ids));
    }

    public void InitiateOpenStateTasks(@RequestBody TimedTaskVO timedTaskVO) {

        TimedTask timedTask = new TimedTask();
        BeanUtils.copyProperties(timedTaskVO , timedTask);
        if (timedTaskVO.getFunction() != null){
            timedTask.setFunctionArray( String.join(",", timedTaskVO.getFunction()));
        }

        /** 开启定时任务*/
        List<SwitchLoginInformation> switchLoginInformations = TimedTaskRetrievalFile.readCiphertextExcel(MyUtils.getProjectPath()+"\\jobExcel\\"+ timedTask.getTimedTaskParameters() +".txt");
        if (MyUtils.isCollectionEmpty(switchLoginInformations)){
            return;
        }
        /* 任务间隔时间 秒 */
        Integer intervalSecond = convertToSeconds(timedTaskVO.getTimedTaskIntervalTime());

        sysUserService = SpringBeanUtil.getBean(ISysUserService.class);
        SysUser sysUser = sysUserService.selectUserByUserName(timedTaskVO.getCreatorName());

        LoginUser loginUser = new LoginUser();
        loginUser.setUser(sysUser);
        loginUser.setUserId(sysUser.getUserId());
        loginUser.setDeptId(sysUser.getDeptId());

        if (timedTaskVO.getTimedTaskStartTime()!=null){

            /* 为根据固定时间开始扫描*/
            /* 根据当前时间开始扫描*/
            Timer timer = new Timer();
            timerStorage.put(timedTaskVO.getId(),timer);

            TemplateScheduledTasks task = new TemplateScheduledTasks( switchLoginInformations, timer,  timedTaskVO,loginUser);/*交换机登录方式*/

            Date date = timedTaskVO.getTimedTaskStartTime();

            // 使用Calendar类获取年、月、日、时、分、秒
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Date startTime = calendar.getTime();

            /* long period = time * 60 * 1000; // 执行间隔，单位为毫秒*/
            timer.schedule(task, startTime, intervalSecond * 1000);

        }else {
            /* 根据当前时间开始扫描*/
            Timer timer = new Timer();
            timerStorage.put(timedTaskVO.getId(),timer);

            TemplateScheduledTasks task = new TemplateScheduledTasks( switchLoginInformations, timer,  timedTaskVO,loginUser);/*交换机登录方式*/
                /*long delay = 0; // 延迟时间，单位为毫秒
                long period = time * 60 * 1000; // 执行间隔，单位为毫秒*/
            timer.schedule(task, 0, intervalSecond * 1000);
        }
    }

    @PutMapping("/performScheduledTasks")
    @MyLog(title = "定时任务", businessType = BusinessType.UPDATE)
    public void performScheduledTasks(@RequestBody TimedTaskVO timedTaskVO) {
        /*将TimedTaskVO 转为 TimedTask ，将扫描功能集合拼接成字符串*/
        TimedTask timedTask = new TimedTask();
        BeanUtils.copyProperties(timedTaskVO , timedTask);
        if (timedTaskVO.getFunction() != null){
            timedTask.setFunctionArray( String.join(",", timedTaskVO.getFunction()));
        }

        /* 插入 */
        timedTaskService = SpringBeanUtil.getBean(ITimedTaskService.class);
        int i = timedTaskService.updateTimedTask(timedTask);

        /* 任务状态为 1  则是关闭定时任务*/
        if (timedTask.getTimedTaskStatus() == 1){
            /* 定时器获取*/
            Timer timer = timerStorage.get(timedTask.getId());
            /* 定时器移除*/
            timerStorage.remove(timedTask.getId());
            /* 定时器关闭*/
            timer.cancel();
            return;
        }

        /* 根据定时任务模板  获取交换机登录信息 */
        List<SwitchLoginInformation> switchLoginInformations = TimedTaskRetrievalFile.readCiphertextExcel(MyUtils.getProjectPath()+"\\jobExcel\\"+ timedTask.getTimedTaskParameters() +".txt");
        if (MyUtils.isCollectionEmpty(switchLoginInformations)){
            return;
        }

        /* 任务间隔时间 以秒为单位 */
        Integer intervalSecond = convertToSeconds(timedTaskVO.getTimedTaskIntervalTime());
        /* 开启定时任务的用户信息*/
        LoginUser loginUser = SecurityUtils.getLoginUser();

        if (i>0){
            if (timedTaskVO.getTimedTaskStartTime()!=null){

                /* 为根据固定时间开始扫描*/
                /* 根据当前时间开始扫描*/
                Timer timer = new Timer();

                timerStorage.put(timedTaskVO.getId(),timer);

                TemplateScheduledTasks task = new TemplateScheduledTasks( switchLoginInformations, timer,  timedTaskVO,loginUser);/*交换机登录方式*/

                Date date = timedTaskVO.getTimedTaskStartTime();

                // 使用Calendar类获取年、月、日、时、分、秒
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                Date startTime = calendar.getTime();

                /* long period = time * 60 * 1000; // 执行间隔，单位为毫秒*/
                timer.schedule(task, startTime, intervalSecond * 1000);

            }else {
                /* 根据当前时间开始扫描*/
                Timer timer = new Timer();
                timerStorage.put(timedTaskVO.getId(),timer);

                TemplateScheduledTasks task = new TemplateScheduledTasks( switchLoginInformations, timer,  timedTaskVO,loginUser);/*交换机登录方式*/
                /*long delay = 0; // 延迟时间，单位为毫秒
                long period = time * 60 * 1000; // 执行间隔，单位为毫秒*/
                timer.schedule(task, 0, intervalSecond * 1000);
            }
        }

    }

    /** 时间转化为 秒 */
    public Integer convertToSeconds(String time) {
        time = time.replace("：",":");
        String[] time_split = time.split(":");
        int second = 0;
        for (int i=0;i<time_split.length;i++){
            if (time_split[i].equals("00")){
                continue;
            }
            switch (i){
                case 0:
                    /*时*/
                    second += Integer.valueOf(time_split[i]).intValue() * 60 * 60 ;
                    break;
                case 1:
                    /*分*/
                    second += Integer.valueOf(time_split[i]).intValue() * 60 ;
                    break;
                case 2:
                    /*秒*/
                    second += Integer.valueOf(time_split[i]).intValue() ;
                    break;
            }
        }
        return second;
    }

}