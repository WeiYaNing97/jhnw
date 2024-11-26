package com.sgcc.advanced.controller;
import com.alibaba.fastjson.JSON;
import com.sgcc.advanced.thread.AdvancedThreadPool;
import com.sgcc.advanced.utils.AdvancedUtils;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.util.WorkThreadMonitor;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = "运行分析")
@RestController
@RequestMapping("/advanced/AdvancedFeatures")
@Transactional(rollbackFor = Exception.class)
public class AdvancedFeatures {


    HashMap<String, AdvancedThreadPool> advancedThreadPoolHashMap = new HashMap<>();

    /**
     * 运行分析接口
     *
     * @param switchInformation 用户登录信息列表，格式为json字符串
     * @param scanNum           扫描允许最大线程数
     * @param functionName    功能名称列表
     * @return 返回扫描结果
     */
    @ApiOperation("运行分析接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "switchInformation", value = "用户登录信息列表，格式为json字符串",dataTypeClass = List.class, required = true),
            @ApiImplicitParam(name = "scanNum", value = "扫描允许最大线程数",dataTypeClass = Long.class,required = true),
            @ApiImplicitParam(name = "functionName", value = "功能名称列表",dataTypeClass = List.class, required = true)
    })
    @PostMapping("/advancedFunction/{scanNum}/{functionName}")
    @MyLog(title = "运行分析", businessType = BusinessType.OTHER)
    public String advancedFunction(@RequestBody List<String> switchInformation,
                                   @PathVariable Long scanNum,
                                   @PathVariable List<String> functionName) {

        // 转换用户登录信息列表为SwitchParameters列表
        List<SwitchParameters> switchParameters = AdvancedUtils.convertSwitchInformation(switchInformation);
        // 创建参数集对象，将用户登录信息列表和扫描次数传入
        ParameterSet parameterSet = createParameterSet(switchParameters, scanNum);

        /*设置线程中断标志*/
        WorkThreadMonitor.setShutdownFlag(parameterSet.getLoginUser().getUsername(),false);

        try {
            // 调用高级功能线程池执行登录信息操作
            //boolean isRSA = true; //前端数据是否通过 RSA 加密后传入后端
            executeAdvancedFunction(parameterSet, functionName, true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*移除线程中断标志*/
        WorkThreadMonitor.removeThread(parameterSet.getLoginUser().getUsername());


        /* todo 存在问题 前端无法接受全部后端传入WebSocket数据*/

        // 发送WebSocket消息，传输登录人姓名和问题简述
        //传输登陆人姓名 及问题简述
        WebSocketService webSocketService = new WebSocketService();
        webSocketService.sendMessage(parameterSet.getLoginUser().getUsername(),"接收："+"扫描结束\r\n");

        try {
            // 将问题简述和问题路径写入文件
            PathHelper.writeDataToFile("接收："+"扫描结束\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 格式化当前时间加上10分钟后的时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime_10 = dateFormat.format(new Date(new Date().getTime() + 600000));

        // 等待条件满足后返回扫描结束字符串
        while (true){
            if (WebSocketService.userMap.get(parameterSet.getLoginUser().getUsername()) != null){
                // 如果WebSocket连接存在，则移除连接并返回扫描结束
                WebSocketService.userMap.remove(parameterSet.getLoginUser().getUsername());
                return "扫描结束";
            }
            if (dateFormat.format(new Date(new Date().getTime())).compareTo(nowTime_10) >=0 ){
                // 如果等待时间超过10分钟，则返回扫描结束
                return "扫描结束";
            }
        }
    }



    /**
     * 创建ParameterSet对象并设置相关参数。
     *
     * @param switchParametersList SwitchParameters列表
     * @param scanNum              扫描次数
     * @return 创建的ParameterSet对象
     */
    private ParameterSet createParameterSet(List<SwitchParameters> switchParametersList, Long scanNum) {
        // 创建ParameterSet对象
        ParameterSet parameterSet = new ParameterSet();
        // 设置SwitchParameters列表
        parameterSet.setSwitchParameters(switchParametersList);
        // 设置登录用户信息
        parameterSet.setLoginUser(SecurityUtils.getLoginUser());
        // 设置线程数，将扫描次数转换为整数并设置
        parameterSet.setThreadCount(Integer.valueOf(scanNum + "").intValue());
        // 返回创建的ParameterSet对象
        return parameterSet;
    }


    /**
     * 调用高级功能线程池执行登录信息操作。
     *
     * 本方法利用线程池异步执行登录信息的操作，旨在提高执行效率和处理性能。
     * 它适用于需要对登录信息进行批量或复杂操作的场景，通过指定不同的功能名称列表，
     * 可以实现包括但不限于更新、验证等操作。注意，本方法默认假设传入的参数通过RSA加密，
     * 除非明确指定不使用加密。
     *
     * @param parameterSet    包含登录信息和其他参数的ParameterSet对象
     * @param functionName    功能名称列表，每个名称对应一个具体操作
     * @param isRSA           是否通过RSA加密后端传入的数据，默认为true
     * @throws InterruptedException 线程中断异常，当线程在等待状态中被中断时抛出
     */
    private void executeAdvancedFunction(ParameterSet parameterSet, List<String> functionName, boolean isRSA) throws InterruptedException {

        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdownFlag(parameterSet.getLoginUser().getUsername())){
            // 如果线程中断标志为true，则直接返回
            return;
        }

        // 调用高级线程池具体实现类中的方法来执行登录信息的操作
        AdvancedThreadPool advancedThreadPool = new AdvancedThreadPool();
        advancedThreadPoolHashMap.put(parameterSet.getLoginUser().getUsername(), advancedThreadPool);

        advancedThreadPool.switchLoginInformations(parameterSet, functionName, isRSA);
        advancedThreadPoolHashMap.remove(parameterSet.getLoginUser().getUsername());
    }


    /**
     * 终止高级线程池。
     *
     * 该方法用于终止指定用户的高级线程池，并从HashMap中移除对应的线程池对象。
     */
    /* 全面扫描终止 */
    @PostMapping("/advancedFunctionTerminationScann")
    public void advancedFunctionTerminationScann() {
        String username = SecurityUtils.getLoginUser().getUsername();
        AdvancedThreadPool advancedThreadPool = advancedThreadPoolHashMap.get(username);
        advancedThreadPool.terminationScanThread();
        advancedThreadPoolHashMap.remove(username);
    }

}
