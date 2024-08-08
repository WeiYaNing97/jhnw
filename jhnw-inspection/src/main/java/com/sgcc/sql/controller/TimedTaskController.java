package com.sgcc.sql.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.domain.entity.SysUser;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.util.MyUtils;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.ITotalQuestionTableService;
import com.sgcc.sql.util.TemplateScheduledTasks;
import com.sgcc.sql.util.TimedTaskRetrievalFile;
import com.sgcc.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
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
import com.sgcc.sql.domain.TimedTask;
import com.sgcc.sql.service.ITimedTaskService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 定时任务Controller
 *
 * @author ruoyi
 * @date 2024-04-03
 */
@Api(tags = "定时任务管理")
@RestController
@RequestMapping("/sql/TimedTask")
public class TimedTaskController extends BaseController
{
    @Autowired
    private ITimedTaskService timedTaskService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ITotalQuestionTableService totalQuestionTableService;
    public static HashMap<Long,Timer> timerStorage = new HashMap<>();

    @Value("${TimedTasks}")
    public String TimedTasks;
    /**
     * 导出定时任务列表
     */
    @ApiOperation(value = "导出定时任务列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "定时任务编号", dataType = "Long"),
            @ApiImplicitParam(name = "timedTaskName", value = "定时任务名称", dataType = "String"),
            @ApiImplicitParam(name = "timedTaskParameters", value = "定时任务模板", dataType = "String"),
            @ApiImplicitParam(name = "timedTaskStartTime", value = "定时任务开始时间", dataType = "Date"),
            @ApiImplicitParam(name = "timedTaskIntervalTime", value = "定时任务间隔时间", dataType = "String"),
            @ApiImplicitParam(name = "functionArray", value = "功能数组", dataType = "String"),
            @ApiImplicitParam(name = "timedTaskStatus", value = "定时任务开启状态", dataType = "Integer"),
            @ApiImplicitParam(name = "creatorName", value = "定时任务创建人姓名", dataType = "String"),
            @ApiImplicitParam(name = "createdOn", value = "定时任务创建时间", dataType = "Date")
    })
    @PreAuthorize("@ss.hasPermi('sql:TimedTask:export')")
    @MyLog(title = "定时任务", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(TimedTask timedTask)
    {
        List<TimedTask> list = timedTaskService.selectTimedTaskList(timedTask);
        ExcelUtil<TimedTask> util = new ExcelUtil<TimedTask>(TimedTask.class);
        return util.exportExcel(list, "定时任务数据");
    }

    /**
     * 新增定时任务
     */
    @ApiOperation(value = "新增定时任务")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "定时任务编号", dataType = "Long"),
            @ApiImplicitParam(name = "timedTaskName", value = "定时任务名称", dataType = "String"),
            @ApiImplicitParam(name = "timedTaskParameters", value = "定时任务参数", dataType = "String"),
            @ApiImplicitParam(name = "timedTaskStartTime", value = "定时任务开始时间", dataType = "Date"),
            @ApiImplicitParam(name = "timedTaskIntervalTime", value = "定时任务间隔时间", dataType = "String"),
            @ApiImplicitParam(name = "functionalTree", value = "所有功能集合", dataType = "List<FunctionVO>"),
            @ApiImplicitParam(name = "selectFunctions", value = "选择功能集合", dataType = "List<String>"),
            @ApiImplicitParam(name = "functionName", value = "功能集合", dataType = "List<String>"),
            @ApiImplicitParam(name = "timedTaskStatus", value = "定时任务开启状态", dataType = "Integer"),
            @ApiImplicitParam(name = "creatorName", value = "定时任务创建人姓名", dataType = "String"),
            @ApiImplicitParam(name = "createdOn", value = "定时任务创建时间", dataType = "Date"),
            @ApiImplicitParam(name = "selectFunctionWindow", value = "选择功能窗口", dataType = "boolean")
    })
    @PreAuthorize("@ss.hasPermi('sql:TimedTask:add')")
    @MyLog(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TimedTaskVO timedTaskVO)
    {
        // 创建一个新的定时任务对象
        TimedTask timedTask = new TimedTask();
        // 将前端传递的VO对象属性复制到定时任务对象中
        BeanUtils.copyProperties(timedTaskVO , timedTask);

        // 如果前端传递了选中的功能列表
        if (timedTaskVO.getSelectFunctions() != null){
            // 将选中的功能列表转换为字符串，并去掉字符串首尾的方括号，然后赋值给定时任务对象的functionArray属性
            timedTask.setFunctionArray((timedTaskVO.getSelectFunctions()+"").substring(1,(timedTaskVO.getSelectFunctions()+"").length()-1));
        }
        // 将定时任务的时间间隔字符串中的中文冒号替换为英文冒号
        timedTask.setTimedTaskIntervalTime(timedTask.getTimedTaskIntervalTime().replace("：",":"));

        // 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 设置定时任务对象的创建者名称为当前登录用户的用户名
        timedTask.setCreatorName(loginUser.getUsername());

        // 调用服务层的插入定时任务方法，并将结果返回给前端
        return toAjax(timedTaskService.insertTimedTask(timedTask));
    }

    /**
     * 修改定时任务
     */
    @ApiOperation(value = "修改定时任务")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "定时任务编号", dataType = "Long"),
            @ApiImplicitParam(name = "timedTaskName", value = "定时任务名称", dataType = "String"),
            @ApiImplicitParam(name = "timedTaskParameters", value = "定时任务参数", dataType = "String"),
            @ApiImplicitParam(name = "timedTaskStartTime", value = "定时任务开始时间", dataType = "Date"),
            @ApiImplicitParam(name = "timedTaskIntervalTime", value = "定时任务间隔时间", dataType = "String"),
            @ApiImplicitParam(name = "functionalTree", value = "所有功能集合", dataType = "List<FunctionVO>"),
            @ApiImplicitParam(name = "selectFunctions", value = "选择功能集合", dataType = "List<String>"),
            @ApiImplicitParam(name = "functionName", value = "功能集合", dataType = "List<String>"),
            @ApiImplicitParam(name = "timedTaskStatus", value = "定时任务开启状态", dataType = "Integer"),
            @ApiImplicitParam(name = "creatorName", value = "定时任务创建人姓名", dataType = "String"),
            @ApiImplicitParam(name = "createdOn", value = "定时任务创建时间", dataType = "Date"),
            @ApiImplicitParam(name = "selectFunctionWindow", value = "选择功能窗口", dataType = "boolean")
    })
    @PreAuthorize("@ss.hasPermi('sql:TimedTask:edit')")
    @MyLog(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TimedTaskVO timedTaskVO)
    {
        // 创建一个新的定时任务对象
        TimedTask timedTask = new TimedTask();
        // 将前端传递的VO对象属性复制到定时任务对象中
        BeanUtils.copyProperties(timedTaskVO , timedTask);
        // 将定时任务的时间间隔字符串中的中文冒号替换为英文冒号
        timedTask.setTimedTaskIntervalTime(timedTask.getTimedTaskIntervalTime().replace("：",":"));
        // 如果前端传递了选中的功能列表
        if (timedTaskVO.getSelectFunctions() != null){
            // 将选中的功能列表转换为字符串，并去掉字符串首尾的方括号，然后赋值给定时任务对象的functionArray属性
            timedTask.setFunctionArray((timedTaskVO.getSelectFunctions()+"").substring(1,(timedTaskVO.getSelectFunctions()+"").length()-1));
        }

        // 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 设置定时任务对象的创建者名称为当前登录用户的用户名
        timedTask.setCreatorName(loginUser.getUsername());

        // 调用服务层的更新定时任务方法，并将结果返回给前端
        return toAjax(timedTaskService.updateTimedTask(timedTask));
    }

    /**
     * 删除定时任务
     */
    @ApiOperation(value = "删除定时任务")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "ids", value = "定时任务编号", dataType = "Long[]")
    })
    @PreAuthorize("@ss.hasPermi('sql:TimedTask:remove')")
    @MyLog(title = "定时任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){

        return toAjax(timedTaskService.deleteTimedTaskByIds(ids));
    }

    /**
     * 初始化开启状态任务
     *
     * @param timedTaskVO 定时任务对象VO
     */
    public void InitiateOpenStateTasks(@RequestBody TimedTaskVO timedTaskVO) {
        // 创建一个新的定时任务对象
        TimedTask timedTask = new TimedTask();
        // 将传入的VO对象的属性复制到定时任务对象中
        BeanUtils.copyProperties(timedTaskVO , timedTask);

        // 如果传入的VO对象中有函数名称
        if (timedTaskVO.getFunctionName() != null){
            // 将函数名称以逗号分隔，并赋值给定时任务对象的functionArray属性
            timedTask.setFunctionArray( String.join(",", timedTaskVO.getFunctionName()));
        }

        /**
         * 开启定时任务
         */
        // 从指定的路径中读取加密的Excel文件，获取交换机登录信息列表
        List<SwitchLoginInformation> switchLoginInformations = TimedTaskRetrievalFile.readCiphertextExcel(MyUtils.getProjectPath()+"\\jobExcel\\"+ timedTask.getTimedTaskParameters() +".txt");

        // 如果交换机登录信息列表为空
        if (MyUtils.isCollectionEmpty(switchLoginInformations)){
            // 直接返回
            return;
        }

        // 将定时任务的间隔时间转换为秒
        /* 任务间隔时间 秒 */
        Integer intervalSecond = convertToSeconds(timedTaskVO.getTimedTaskIntervalTime());

        // 获取SysUserService接口的实例
        sysUserService = SpringBeanUtil.getBean(ISysUserService.class);
        // 根据创建者名称查询SysUser对象
        SysUser sysUser = sysUserService.selectUserByUserName(timedTaskVO.getCreatorName());

        // 创建一个LoginUser对象
        LoginUser loginUser = new LoginUser();
        // 设置LoginUser对象的user属性为查询到的SysUser对象
        loginUser.setUser(sysUser);
        // 设置LoginUser对象的userId属性为SysUser对象的userId
        loginUser.setUserId(sysUser.getUserId());
        // 设置LoginUser对象的deptId属性为SysUser对象的deptId
        loginUser.setDeptId(sysUser.getDeptId());

        // 如果传入的VO对象中有定时任务的开始时间
        if (timedTaskVO.getTimedTaskStartTime()!=null){
            /*
             * 为根据固定时间开始扫描
             * 根据当前时间开始扫描
             */
            // 创建一个Timer对象
            Timer timer = new Timer();
            // 将定时任务ID和Timer对象存入timerStorage中
            timerStorage.put(timedTaskVO.getId(),timer);

            // 创建一个TemplateScheduledTasks对象，并传入交换机登录信息列表、Timer对象、VO对象和LoginUser对象
            TemplateScheduledTasks task = new TemplateScheduledTasks( switchLoginInformations, timer,  timedTaskVO,loginUser);/*交换机登录方式*/

            // 获取定时任务的开始时间
            Date date = timedTaskVO.getTimedTaskStartTime();

            // 使用Calendar类获取开始时间的年、月、日、时、分、秒
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Date startTime = calendar.getTime();

            // 使用定时任务的间隔时间（秒）和1000的乘积作为执行间隔（毫秒），并启动定时任务
            /* long period = time * 60 * 1000; // 执行间隔，单位为毫秒*/
            timer.schedule(task, startTime, intervalSecond * 1000);

        } else {
            /*
             * 根据当前时间开始扫描
             */
            // 创建一个Timer对象
            Timer timer = new Timer();
            // 将定时任务ID和Timer对象存入timerStorage中
            timerStorage.put(timedTaskVO.getId(),timer);

            // 创建一个TemplateScheduledTasks对象，并传入交换机登录信息列表、Timer对象、VO对象和LoginUser对象
            TemplateScheduledTasks task = new TemplateScheduledTasks( switchLoginInformations, timer,  timedTaskVO,loginUser);/*交换机登录方式*/

            // 使用定时任务的间隔时间（秒）和1000的乘积作为执行间隔（毫秒），并立即启动定时任务
                /*long delay = 0; // 延迟时间，单位为毫秒
                long period = time * 60 * 1000; // 执行间隔，单位为毫秒*/
            timer.schedule(task, 0, intervalSecond * 1000);
        }
    }


    /**
     * 修改定时任务状态
     *
     * @param timedTaskVO 定时任务对象
     * @return 无返回值
     */
    @ApiOperation(value = "修改定时任务状态")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "定时任务编号", dataType = "Long"),
            @ApiImplicitParam(name = "timedTaskName", value = "定时任务名称", dataType = "String"),
            @ApiImplicitParam(name = "timedTaskParameters", value = "定时任务参数", dataType = "String"),
            @ApiImplicitParam(name = "timedTaskStartTime", value = "定时任务开始时间", dataType = "Date"),
            @ApiImplicitParam(name = "timedTaskIntervalTime", value = "定时任务间隔时间", dataType = "String"),
            @ApiImplicitParam(name = "functionalTree", value = "所有功能集合", dataType = "List<FunctionVO>"),
            @ApiImplicitParam(name = "selectFunctions", value = "选择功能集合", dataType = "List<String>"),
            @ApiImplicitParam(name = "functionName", value = "功能集合", dataType = "List<String>"),
            @ApiImplicitParam(name = "timedTaskStatus", value = "定时任务开启状态", dataType = "Integer"),
            @ApiImplicitParam(name = "creatorName", value = "定时任务创建人姓名", dataType = "String"),
            @ApiImplicitParam(name = "createdOn", value = "定时任务创建时间", dataType = "Date"),
            @ApiImplicitParam(name = "selectFunctionWindow", value = "选择功能窗口", dataType = "boolean")
    })
    @PutMapping("/performScheduledTasks")
    @MyLog(title = "定时任务", businessType = BusinessType.UPDATE)
    public void performScheduledTasks(@RequestBody TimedTaskVO timedTaskVO) {
        // 获取当前登录用户信息
        /* 开启定时任务的用户信息*/
        LoginUser loginUser = SecurityUtils.getLoginUser();

        // 创建一个新的TimedTask对象，并将TimedTaskVO的属性复制到TimedTask对象中
        /*将TimedTaskVO 转为 TimedTask ，将扫描功能集合拼接成字符串*/
        TimedTask timedTask = new TimedTask();
        BeanUtils.copyProperties(timedTaskVO , timedTask);

        // 如果TimedTaskVO中的selectFunctions不为空，则将其转换为字符串并去除首尾的方括号，然后赋值给TimedTask的functionArray属性
        if (timedTaskVO.getSelectFunctions() != null){
            timedTask.setFunctionArray((timedTaskVO.getSelectFunctions()+"").substring(1,(timedTaskVO.getSelectFunctions()+"").length()-1));
        }

        // 设置定时任务的创建者名称为当前登录用户的用户名
        timedTask.setCreatorName(loginUser.getUsername());

        // 调用timedTaskService的updateTimedTask方法更新定时任务，并将结果赋值给变量i
        // 更新定时任务
        /* 插入 */
        timedTaskService = SpringBeanUtil.getBean(ITimedTaskService.class);
        int i = timedTaskService.updateTimedTask(timedTask);

        // 如果更新失败（即i小于0）
        if (i<0){
            // 发送问题日志报警信息，提示定时任务状态修改失败
            AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), "问题日志",
                    timedTaskVO.getTimedTaskName() + "定时任务状态修改失败");
        }

        // 如果定时任务的状态为1（即关闭状态）
        if (timedTask.getTimedTaskStatus() == 1){
            // 获取存储中对应id的定时器
            /* 定时器获取*/
            Timer timer = timerStorage.get(timedTask.getId());

            // 从存储中移除对应id的定时器
            /* 定时器移除*/
            timerStorage.remove(timedTask.getId());

            // 关闭定时器
            /* 定时器关闭*/
            timer.cancel();

            // 返回，不再继续执行后续代码
            return;
        }

        // 根据定时任务参数读取交换机登录信息列表
        /* 根据定时任务模板  获取交换机登录信息 */
        List<SwitchLoginInformation> switchLoginInformations = TimedTaskRetrievalFile.readCiphertextExcel(MyUtils.getProjectPath()+"\\jobExcel\\"+ timedTask.getTimedTaskParameters() +".txt");

        // 如果交换机登录信息列表为空，则直接返回，不再继续执行后续代码
        if (MyUtils.isCollectionEmpty(switchLoginInformations)){
            return;
        }

        // 将定时任务的间隔时间转换为秒
        /* 任务间隔时间 以秒为单位 */
        Integer intervalSecond = convertToSeconds(timedTaskVO.getTimedTaskIntervalTime());

        // 如果TimedTaskVO中的startTime不为空
        if (timedTaskVO.getTimedTaskStartTime()!=null){
            // 创建一个新的Timer对象
            /* 为根据固定时间开始扫描*/
            /* 根据当前时间开始扫描*/
            Timer timer = new Timer();

            // 将新创建的Timer对象存入存储中，key为定时任务的id
            // 将定时器存入存储
            timerStorage.put(timedTaskVO.getId(),timer);

            // 创建TemplateScheduledTasks对象，并设置交换机登录信息、定时器、TimedTaskVO和登录用户
            TemplateScheduledTasks task = new TemplateScheduledTasks( switchLoginInformations, timer,  timedTaskVO,loginUser);/*交换机登录方式*/

            // 获取定时任务的开始时间
            Date date = timedTaskVO.getTimedTaskStartTime();

            // 使用Calendar类获取开始时间的年、月、日、时、分、秒
            // 获取定时任务的开始时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Date startTime = calendar.getTime();

            // 定时执行任务，间隔时间为intervalSecond * 1000毫秒（即intervalSecond秒）
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

    public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 查询定时任务列表
     */
    @ApiOperation(value = "查询定时任务列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "定时任务编号", dataType = "Long"),
            @ApiImplicitParam(name = "timedTaskName", value = "定时任务名称", dataType = "String"),
            @ApiImplicitParam(name = "timedTaskParameters", value = "定时任务参数", dataType = "String"),
            @ApiImplicitParam(name = "timedTaskStartTime", value = "定时任务开始时间", dataType = "Date"),
            @ApiImplicitParam(name = "timedTaskIntervalTime", value = "定时任务间隔时间", dataType = "String"),
            @ApiImplicitParam(name = "functionalTree", value = "所有功能集合", dataType = "List<FunctionVO>"),
            @ApiImplicitParam(name = "selectFunctions", value = "选择功能集合", dataType = "List<String>"),
            @ApiImplicitParam(name = "functionName", value = "功能集合", dataType = "List<String>"),
            @ApiImplicitParam(name = "timedTaskStatus", value = "定时任务开启状态", dataType = "Integer"),
            @ApiImplicitParam(name = "creatorName", value = "定时任务创建人姓名", dataType = "String"),
            @ApiImplicitParam(name = "createdOn", value = "定时任务创建时间", dataType = "Date"),
            @ApiImplicitParam(name = "selectFunctionWindow", value = "选择功能窗口", dataType = "boolean")
    })
    @PreAuthorize("@ss.hasPermi('sql:TimedTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(TimedTaskVO timedTaskVO) {
        /* 前端VO转化为数据库实体类*/
        TimedTask timedTask = new TimedTask();
        BeanUtils.copyProperties(timedTaskVO , timedTask);
        if (timedTaskVO.getFunctionName() != null){
            timedTask.setFunctionArray( String.join(",", timedTaskVO.getFunctionName()));
        }

        startPage();
        /* 所有定时任务 */
        List<TimedTask> list = timedTaskService.selectTimedTaskList(timedTask);

        List<TimedTaskVO> timedTaskVOS = new ArrayList<>();
        for (TimedTask pojo:list){
            TimedTaskVO pojoVO = new TimedTaskVO();
            BeanUtils.copyProperties(pojo,pojoVO);

            if (pojo.getFunctionArray()!=null){
                String[] functionArray = pojo.getFunctionArray().split(",");

                List<String> functionIDList = new ArrayList<>();
                List<String> functionNameList = new ArrayList<>();
                List<String> selectFunctions = new ArrayList<>();
                for (String function:functionArray){
                    function = function.trim();
                    selectFunctions.add(function);
                    boolean isAPureNumber = MyUtils.determineWhetherAStringIsAPureNumber(function);
                    if (isAPureNumber){
                        functionIDList.add(function);
                    }else {
                        functionNameList.add(function);
                    }
                }

                pojoVO.setSelectFunctions(selectFunctions);

                if (functionIDList.size() != 0){

                    /* 安全配置问题ID*/
                    Long[] functionlongs = functionIDList.stream().map(Long::parseLong).toArray(Long[]::new);
                    List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableByIds(functionlongs);

                    /* 安全配置问题 名称*/
                    List<String> functionsName = totalQuestionTables.stream().map(x -> x.getTemProName() + "-" + x.getProblemName()).collect(Collectors.toList());

                    /* 添加日常巡检和 运行分析 问题名称*/
                    functionNameList.addAll(functionsName);

                }

                pojoVO.setFunctionName( functionNameList);
            }

            timedTaskVOS.add(pojoVO);
        }

        return getDataTable(timedTaskVOS);
    }


    /**
     * 获取定时任务全部功能
     * @return
     */
    @ApiOperation(value = "获取定时任务全部功能")
    @GetMapping("/getFunction")
    public List<FunctionVO> getFunction() {
        /*获取问题集合 及 范式分类集合 */
        List<TotalQuestionTable> totalQuestionTableList = totalQuestionTableService.scanningSQLselectTotalQuestionTableList();
        Set<String> collect = totalQuestionTableList.stream().map(TotalQuestionTable::getTypeProblem).collect(Collectors.toSet());
        collect.add("运行分析");

        /* 创建一个范式分类为key的 map集合*/
        HashMap<String,List<FunctionName>> functionNameListMap = new HashMap<>();
        for (String typeProblem:collect){
            functionNameListMap.put(typeProblem,new ArrayList<>());
        }

        /* 将 问题表数据加入 树型插件*/
        for (TotalQuestionTable totalQuestionTable:totalQuestionTableList){
            List<FunctionName> functionNames = functionNameListMap.get(totalQuestionTable.getTypeProblem());

            FunctionName functionName = new FunctionName();
            functionName.setId(totalQuestionTable.getId()+"");
            functionName.setLabel(totalQuestionTable.getTemProName()+"-"+totalQuestionTable.getProblemName());

            functionNames.add(functionName);
            functionNameListMap.put(totalQuestionTable.getTypeProblem(),functionNames);
        }
        /* 将 运行分析加入 树型插件*/
        String[] TimedTasksSplit = TimedTasks.split(" ");
        for (String timedTask:TimedTasksSplit){
            List<FunctionName> functionNames = functionNameListMap.get("运行分析");

            FunctionName functionName = new FunctionName();
            functionName.setId(timedTask);
            functionName.setLabel(timedTask);

            functionNames.add(functionName);
            functionNameListMap.put("运行分析",functionNames);
        }

        List<FunctionVO> functionVOList = new ArrayList<>();
        for (String typeProblem:collect){
            List<FunctionName> functionNames = functionNameListMap.get(typeProblem);

            FunctionVO functionVO = new FunctionVO();
            functionVO.setLabel(typeProblem);
            functionVO.setChildren(functionNames);

            functionVOList.add(functionVO);

        }

        return functionVOList;
    }


    /** 获取定时任务详细信息 */
    @ApiOperation(value = "获取定时任务详细信息")
    @PreAuthorize("@ss.hasPermi('sql:TimedTask:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {   TimedTask timedTask = timedTaskService.selectTimedTaskById(id);

        TimedTaskVO timedTaskVO = new TimedTaskVO();
        BeanUtils.copyProperties(timedTask,timedTaskVO);

        if (timedTask.getFunctionArray()!=null){

            String[] functionArray = timedTask.getFunctionArray().split(",");

            List<String> functionIDList = new ArrayList<>();
            List<String> functionNameList = new ArrayList<>();
            List<String> selectFunctions = new ArrayList<>();
            for (String function:functionArray){
                function = function.trim();
                selectFunctions.add(function);
                boolean isAPureNumber = MyUtils.determineWhetherAStringIsAPureNumber(function);
                if (isAPureNumber){
                    functionIDList.add(function);
                }else {
                    functionNameList.add(function);
                }
            }
            timedTaskVO.setSelectFunctions(selectFunctions);

            if (functionIDList.size() != 0){
                /* 安全配置问题ID*/
                Long[] functionlongs = functionIDList.stream().map(Long::parseLong).toArray(Long[]::new);
                List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableByIds(functionlongs);
                /* 安全配置问题 名称*/
                List<String> functions = totalQuestionTables.stream().map(pojo -> pojo.getTemProName() + "-" + pojo.getProblemName()).collect(Collectors.toList());
                /* 添加日常巡检和运行分析问题名称*/
                functionNameList.addAll(functions);
            }


            /* 所有 自定义问题 */
            List<TotalQuestionTable> totalQuestionTableList = totalQuestionTableService.scanningSQLselectTotalQuestionTableList();
            /* 获取问题树型 和 问题Map*/
            List<Object> function = getFunctionListAndID(totalQuestionTableList);
            List<FunctionVO> functionalTree = (List<FunctionVO>) function.get(0);
            //Map<Long, String> TemProNameProblemNameMap =  (Map<Long, String>) function.get(1);

            /* 功能树型 */
            timedTaskVO.setFunctionalTree(functionalTree);

            timedTaskVO.setFunctionName(functionNameList);

        }

        timedTaskVO.setSelectFunctionWindow(false);

        return AjaxResult.success(timedTaskVO);
    }

    /**
    * @Description 获取全部问题集合树型结构  及  获取全部问题集合
    * @author charles
    * @createTime 2024/4/28 15:33
    * @desc
    * @param totalQuestionTableList
     * @return
    */
    public List<Object> getFunctionListAndID(List<TotalQuestionTable> totalQuestionTableList) {
        /* 定义返回 问题信息
        map集合 key为 问题ID  value为问题名称*/
        Map<String, String> TemProNameProblemNameMap = new HashMap<>();

        /* 筛选范式分类 重新创建一个范式分类SET集合*/
        Set<String> collect = totalQuestionTableList.stream().map(TotalQuestionTable::getTypeProblem).collect(Collectors.toSet());
        collect.add("运行分析");

        /* map集合 用于存储 范式分类下的 问题名称和ID*/
        HashMap<String,List<FunctionName>> functionNameListMap = new HashMap<>();
        for (String typeProblem:collect){
            functionNameListMap.put(typeProblem,new ArrayList<>());
        }

        /* 将 问题表数据加入 树型插件*/
        for (TotalQuestionTable totalQuestionTable:totalQuestionTableList){

            List<FunctionName> functionNames = functionNameListMap.get(totalQuestionTable.getTypeProblem());
            FunctionName functionName = new FunctionName();
            functionName.setId(totalQuestionTable.getId());
            functionName.setLabel(totalQuestionTable.getTemProName()+"-"+totalQuestionTable.getProblemName());
            functionName.setLevel(2);

            TemProNameProblemNameMap.put(totalQuestionTable.getId(),totalQuestionTable.getTemProName()+"-"+totalQuestionTable.getProblemName());
            functionNames.add(functionName);

            functionNameListMap.put(totalQuestionTable.getTypeProblem(),functionNames);
        }

        /* 将 运行分析加入 树型插件*/
        String[] TimedTasksSplit = TimedTasks.split(" ");
        for (String timedTask:TimedTasksSplit){
            List<FunctionName> functionNames = functionNameListMap.get("运行分析");

            FunctionName functionName = new FunctionName();
            functionName.setId(timedTask);
            functionName.setLabel(timedTask);
            functionName.setLevel(2);

            functionNames.add(functionName);
            functionNameListMap.put("运行分析",functionNames);
        }

        List<FunctionVO> functionVOList = new ArrayList<>();
        Long i = 0l;
        for (String typeProblem:collect){
            List<FunctionName> functionNames = functionNameListMap.get(typeProblem);
            FunctionVO functionVO = new FunctionVO();
            functionVO.setId(i++);
            functionVO.setLevel(1);
            functionVO.setLabel(typeProblem);
            functionVO.setChildren(functionNames);
            functionVOList.add(functionVO);
        }

        List<Object> objectList = new ArrayList<>();
        objectList.add(functionVOList);
        objectList.add(TemProNameProblemNameMap);

        return objectList;
    }
}
