package com.sgcc.web.controller.sql;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.domain.ValueInformation;
import com.sgcc.sql.domain.ValueInformationVO;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IValueInformationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 与交换机交互方法类
 * * @E-mail: WeiYaNing97@163.com
 * @date 2022年01月05日 14:18
 */
@RestController
@RequestMapping("/sql/SolveProblemController")
public class SolveProblemController {

    @Autowired
    private ICommandLogicService commandLogicService;
    @Autowired
    private IValueInformationService valueInformationService;

    /***
    * @method: 根据 命令ID commandId 查询命令集合 用于解决问题
    * @Param: [commandId]
    * @return: com.sgcc.common.core.domain.AjaxResult
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("queryCommandSet")
    public AjaxResult queryCommandSet(String commandId){
        Long commandId_Long = Long.valueOf(commandId).longValue();
        List<CommandLogic> commandLogicList = new ArrayList<>();
        do {
            CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId_Long);
            commandLogicList.add(commandLogic);
            commandId_Long = commandLogic.getEndIndex();
        }while (commandId_Long != 0);
        return AjaxResult.success(commandLogicList);
    }

    /***
    * @method: queryParameterSet
    * @Param: []
    * @return: com.sgcc.common.core.domain.AjaxResult
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("queryParameterSet")
    public AjaxResult queryParameterSet(Long parameterID){
        List<ValueInformationVO> valueInformationVOList = new ArrayList<>();
        while (parameterID != 0){
            ValueInformation valueInformation = valueInformationService.selectValueInformationById(parameterID);
            ValueInformationVO valueInformationVO = new ValueInformationVO();
            BeanUtils.copyProperties(valueInformation,valueInformationVO);
            valueInformationVOList.add(valueInformationVO);
            parameterID = valueInformation.getOutId();
        }
        return AjaxResult.success(valueInformationVOList);
    }

}