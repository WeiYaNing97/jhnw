package com.sgcc.sql.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.sgcc.advanced.thread.TimedTaskRetrievalFile;
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
import com.sgcc.system.service.ISysUserService;
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
    @PreAuthorize("@ss.hasPermi('sql:TimedTask:add')")
    @MyLog(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TimedTaskVO timedTaskVO)
    {
        TimedTask timedTask = new TimedTask();
        BeanUtils.copyProperties(timedTaskVO , timedTask);

        if (timedTaskVO.getSelectFunctions() != null){
            timedTask.setFunctionArray((timedTaskVO.getSelectFunctions()+"").substring(1,(timedTaskVO.getSelectFunctions()+"").length()-1));
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        timedTask.setCreatorName(loginUser.getUsername());
        return toAjax(timedTaskService.insertTimedTask(timedTask));
    }

    /**
     * 修改定时任务
     */
    @PreAuthorize("@ss.hasPermi('sql:TimedTask:edit')")
    @MyLog(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TimedTaskVO timedTaskVO)
    {
        TimedTask timedTask = new TimedTask();
        BeanUtils.copyProperties(timedTaskVO , timedTask);

        if (timedTaskVO.getSelectFunctions() != null){
            timedTask.setFunctionArray((timedTaskVO.getSelectFunctions()+"").substring(1,(timedTaskVO.getSelectFunctions()+"").length()-1));
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        timedTask.setCreatorName(loginUser.getUsername());

        return toAjax(timedTaskService.updateTimedTask(timedTask));
    }

    /**
     * 删除定时任务
     */
    @PreAuthorize("@ss.hasPermi('sql:TimedTask:remove')")
    @MyLog(title = "定时任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){

        return toAjax(timedTaskService.deleteTimedTaskByIds(ids));
    }

    public void InitiateOpenStateTasks(@RequestBody TimedTaskVO timedTaskVO) {

        TimedTask timedTask = new TimedTask();
        BeanUtils.copyProperties(timedTaskVO , timedTask);
        if (timedTaskVO.getFunctionName() != null){
            timedTask.setFunctionArray( String.join(",", timedTaskVO.getFunctionName()));
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
        /* 开启定时任务的用户信息*/
        LoginUser loginUser = SecurityUtils.getLoginUser();

        /*将TimedTaskVO 转为 TimedTask ，将扫描功能集合拼接成字符串*/
        TimedTask timedTask = new TimedTask();
        BeanUtils.copyProperties(timedTaskVO , timedTask);

        if (timedTaskVO.getSelectFunctions() != null){
            timedTask.setFunctionArray((timedTaskVO.getSelectFunctions()+"").substring(1,(timedTaskVO.getSelectFunctions()+"").length()-1));
        }

        timedTask.setCreatorName(loginUser.getUsername());

        /* 插入 */
        timedTaskService = SpringBeanUtil.getBean(ITimedTaskService.class);
        int i = timedTaskService.updateTimedTask(timedTask);

        if (i<0){
            AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), "问题日志",
                    timedTaskVO.getTimedTaskName() + "定时任务状态修改失败");
        }

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
    @PreAuthorize("@ss.hasPermi('sql:TimedTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(TimedTaskVO timedTaskVO) {

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

                /* 安全配置问题ID*/
                Long[] functionlongs = functionIDList.stream().map(Long::parseLong).toArray(Long[]::new);
                List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableByIds(functionlongs);

                /* 安全配置问题 名称*/
                List<String> functionsName = totalQuestionTables.stream().map(x -> x.getTemProName() + "-" + x.getProblemName()).collect(Collectors.toList());

                /* 添加日常巡检和高级功能问题名称*/
                functionsName.addAll(functionNameList);

                pojoVO.setFunctionName( functionsName);

            }

            timedTaskVOS.add(pojoVO);
        }

        return getDataTable(timedTaskVOS);
    }


    @GetMapping("/getFunction")
    public List<FunctionVO> getFunction() {
        /*获取问题集合 及 范式分类集合 */
        List<TotalQuestionTable> totalQuestionTableList = totalQuestionTableService.scanningSQLselectTotalQuestionTableList();
        Set<String> collect = totalQuestionTableList.stream().map(TotalQuestionTable::getTypeProblem).collect(Collectors.toSet());
        collect.add("高级功能");

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
        /* 将 高级功能加入 树型插件*/
        String[] TimedTasksSplit = TimedTasks.split(" ");
        for (String timedTask:TimedTasksSplit){
            List<FunctionName> functionNames = functionNameListMap.get("高级功能");

            FunctionName functionName = new FunctionName();
            functionName.setId(timedTask);
            functionName.setLabel(timedTask);

            functionNames.add(functionName);
            functionNameListMap.put("高级功能",functionNames);
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

            /* 安全配置问题ID*/
            Long[] functionlongs = functionIDList.stream().map(Long::parseLong).toArray(Long[]::new);
            List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableByIds(functionlongs);
            /* 安全配置问题 名称*/
            List<String> functions = totalQuestionTables.stream().map(pojo -> pojo.getTemProName() + "-" + pojo.getProblemName()).collect(Collectors.toList());
            /* 添加日常巡检和高级功能问题名称*/
            functions.addAll(functionNameList);

            /* 所有 自定义问题 */
            List<TotalQuestionTable> totalQuestionTableList = totalQuestionTableService.scanningSQLselectTotalQuestionTableList();
            /* 获取问题树型 和 问题Map*/
            List<Object> function = getFunctionListAndID(totalQuestionTableList);
            List<FunctionVO> functionalTree = (List<FunctionVO>) function.get(0);
            //Map<Long, String> TemProNameProblemNameMap =  (Map<Long, String>) function.get(1);

            /* 功能树型 */
            timedTaskVO.setFunctionalTree(functionalTree);

            timedTaskVO.setFunctionName(functions);
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
        map集合 key为问题ID  value为问题名称*/
        Map<Long, String> TemProNameProblemNameMap = new HashMap<>();
        /* 筛选范式分类 重新创建一个范式分类SET集合*/
        Set<String> collect = totalQuestionTableList.stream().map(TotalQuestionTable::getTypeProblem).collect(Collectors.toSet());
        collect.add("高级功能");

        /* map集合 用于存储 范式分类下的 问题名称和ID*/
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
            functionName.setLevel(2);

            TemProNameProblemNameMap.put(totalQuestionTable.getId(),totalQuestionTable.getTemProName()+"-"+totalQuestionTable.getProblemName());
            functionNames.add(functionName);

            functionNameListMap.put(totalQuestionTable.getTypeProblem(),functionNames);
        }

        /* 将 高级功能加入 树型插件*/
        String[] TimedTasksSplit = TimedTasks.split(" ");
        for (String timedTask:TimedTasksSplit){
            List<FunctionName> functionNames = functionNameListMap.get("高级功能");

            FunctionName functionName = new FunctionName();
            functionName.setId(timedTask);
            functionName.setLabel(timedTask);
            functionName.setLevel(2);

            functionNames.add(functionName);
            functionNameListMap.put("高级功能",functionNames);
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
