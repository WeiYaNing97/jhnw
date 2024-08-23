package com.sgcc.advanced.controller;
import com.alibaba.fastjson.JSON;
import com.sgcc.advanced.thread.AdvancedThreadPool;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.PathHelper;
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

    /**
     * 调用运行分析接口，根据扫描次数和功能名称，执行相应的操作。
     *
     * @param switchInformation 用户登录信息列表，格式为json字符串
     * @param scanNum           扫描次数
     * @param functionName      功能名称列表
     * @return 返回扫描结束字符串
     * @throws InterruptedException 线程中断异常
     * @throws IOException 文件写入异常
     */
    @ApiOperation("运行分析接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "switchInformation", value = "用户登录信息列表，格式为json字符串",dataTypeClass = List.class, required = true),
            @ApiImplicitParam(name = "scanNum", value = "扫描允许最大线程数",dataTypeClass = Long.class,required = true),
            @ApiImplicitParam(name = "functionName", value = "功能名称列表",dataTypeClass = List.class, required = true)
    })
    @PostMapping("/advancedFunction/{scanNum}/{functionName}")
    @MyLog(title = "运行分析", businessType = BusinessType.OTHER)
    public String advancedFunction(@RequestBody List<String> switchInformation, @PathVariable Long scanNum, @PathVariable List<String> functionName) {
        // 转换用户登录信息列表为SwitchParameters列表
        List<SwitchParameters> switchParameters = convertSwitchInformation(switchInformation);
        // 创建参数集对象，将用户登录信息列表和扫描次数传入
        ParameterSet parameterSet = createParameterSet(switchParameters, scanNum);

        try {
            // 调用高级功能线程池执行登录信息操作
            //boolean isRSA = true; //前端数据是否通过 RSA 加密后传入后端
            executeAdvancedFunction(parameterSet, functionName, true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        /* todo 存在问题 前端无法接受全部后端传入WebSocket数据*/

        // 发送WebSocket消息，传输登录人姓名和问题简述
        //传输登陆人姓名 及问题简述
        WebSocketService.sendMessage(parameterSet.getLoginUser().getUsername(),"接收："+"扫描结束\r\n");

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
     * 将用户登录信息列表转换为SwitchParameters列表。
     *
     * @param switchInformation 用户登录信息列表
     * @return 转换后的SwitchParameters列表
     */
    private List<SwitchParameters> convertSwitchInformation(List<String> switchInformation) {
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        // 设置当前时间格式为"yyyy-MM-dd HH:mm:ss"
        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        for (String information : switchInformation) {
            // 解析用户登录信息为SwitchLoginInformation对象
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);

            // 创建SwitchParameters对象
            SwitchParameters switchParameters = new SwitchParameters();

            // 设置登录用户信息
            switchParameters.setLoginUser(SecurityUtils.getLoginUser());

            // 设置扫描时间
            switchParameters.setScanningTime(simpleDateFormat);

            // 将SwitchLoginInformation对象的属性复制到SwitchParameters对象中
            BeanUtils.copyBeanProp(switchParameters, switchLoginInformation);

            // 将端口号转换为整数并设置到SwitchParameters对象中
            switchParameters.setPort(Integer.valueOf(switchLoginInformation.getPort()).intValue());

            // 将转换后的SwitchParameters对象添加到列表中
            switchParametersList.add(switchParameters);
        }

        // 返回转换后的SwitchParameters列表
        return switchParametersList;
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
     * @param parameterSet    包含登录信息和其他参数的ParameterSet对象
     * @param functionName    功能名称列表
     * @param isRSA           是否通过RSA加密后传入后端，默认为true
     * @throws InterruptedException 线程中断异常
     */
    private void executeAdvancedFunction(ParameterSet parameterSet, List<String> functionName, boolean isRSA) throws InterruptedException {
        AdvancedThreadPool.switchLoginInformations(parameterSet, functionName, isRSA);
    }

}
