package com.sgcc.advanced.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.sgcc.advanced.controller.ErrorPackage;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.FunctionalMethods;
import com.sgcc.share.util.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.advanced.mapper.ErrorRateCommandMapper;
import com.sgcc.advanced.domain.ErrorRateCommand;
import com.sgcc.advanced.service.IErrorRateCommandService;

/**
 * 错误包命令Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-05-19
 */
@Service
public class ErrorRateCommandServiceImpl implements IErrorRateCommandService 
{
    @Autowired
    private ErrorRateCommandMapper errorRateCommandMapper;

    /**
     * 查询 错误包 命令
     *
     * @param id 错误包 命令主键
     * @return 错误包 命令
     */
    @Override
    public ErrorRateCommand selectErrorRateCommandById(String id)
    {
        return errorRateCommandMapper.selectErrorRateCommandById(id);
    }

    /**
     * 查询 错误包 命令列表
     * 
     * @param errorRateCommand 错误包 命令
     * @return 错误包 命令
     */
    @Override
    public List<ErrorRateCommand> selectErrorRateCommandList(ErrorRateCommand errorRateCommand)
    {

        List<ErrorRateCommand> pojo =new ArrayList<>();
        pojo.addAll(errorRateCommandMapper.selectErrorRateCommandList(errorRateCommand));

        String equivalence = FunctionalMethods.getEquivalence(errorRateCommand.getBrand());
        if (equivalence!=null){
            errorRateCommand.setBrand(equivalence);
            pojo.addAll(errorRateCommandMapper.selectErrorRateCommandList(errorRateCommand));
        }

        return pojo;

    }

    /**
     * 新增 错误包 命令
     * 
     * @param errorRateCommand  错误包 命令
     * @return 结果
     */
    @Override
    public int insertErrorRateCommand(ErrorRateCommand errorRateCommand)
    {
        String regionalCode = (CustomConfigurationUtil.getValue("configuration.problemCode.日常巡检", Constant.getProfileInformation())).toString();
        String id = MyUtils.getID(regionalCode, null);

        errorRateCommand.setId(id);

        return errorRateCommandMapper.insertErrorRateCommand(errorRateCommand);
    }

    /**
     * 修改 错误包 命令
     * 
     * @param errorRateCommand 错误包 命令
     * @return 结果
     */
    @Override
    public int updateErrorRateCommand(ErrorRateCommand errorRateCommand)
    {
        return errorRateCommandMapper.updateErrorRateCommand(errorRateCommand);
    }

    /**
     * 批量删除 错误包 命令
     * 
     * @param ids 需要删除的 错误包 命令主键
     * @return 结果
     */
    @Override
    public int deleteErrorRateCommandByIds(String[] ids)
    {
        return errorRateCommandMapper.deleteErrorRateCommandByIds(ids);
    }

    /**
     * 删除 错误包 命令信息
     * 
     * @param id 错误包 命令主键
     * @return 结果
     */
    @Override
    public int deleteErrorRateCommandById(String id)
    {
        return errorRateCommandMapper.deleteErrorRateCommandById(id);
    }

    @Override
    public List<ErrorRateCommand> selectErrorRateCommandListBySQL(ErrorRateCommand errorRateCommand) {

        List<ErrorRateCommand> pojo = new ArrayList<>();
        pojo.addAll(selectErrorRateCommandListByEquivalence(errorRateCommand));

        String equivalence = FunctionalMethods.getEquivalence(errorRateCommand.getBrand());
        if (equivalence != null) {
            errorRateCommand.setBrand(equivalence);
            pojo.addAll(selectErrorRateCommandListByEquivalence(errorRateCommand));
        }

        return pojo;
    }


    public List<ErrorRateCommand> selectErrorRateCommandListByEquivalence(ErrorRateCommand errorRateCommand) {

        String fuzzySQL = FunctionalMethods.getFuzzySQL(errorRateCommand.getBrand(), errorRateCommand.getSwitchType(),
                errorRateCommand.getFirewareVersion(), errorRateCommand.getSubVersion());

        return errorRateCommandMapper.selectErrorRateCommandListBySQL(fuzzySQL);
    }
}
