package com.sgcc.advanced.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.sgcc.advanced.controller.ErrorPackage;
import com.sgcc.share.util.FunctionalMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.advanced.mapper.ErrorRateCommandMapper;
import com.sgcc.advanced.domain.ErrorRateCommand;
import com.sgcc.advanced.service.IErrorRateCommandService;

/**
 * 误码率命令Service业务层处理
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
     * 查询误码率命令
     *
     * @param id 误码率命令主键
     * @return 误码率命令
     */
    @Override
    public ErrorRateCommand selectErrorRateCommandById(Long id)
    {
        return errorRateCommandMapper.selectErrorRateCommandById(id);
    }

    /**
     * 查询误码率命令列表
     * 
     * @param errorRateCommand 误码率命令
     * @return 误码率命令
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
     * 新增误码率命令
     * 
     * @param errorRateCommand 误码率命令
     * @return 结果
     */
    @Override
    public int insertErrorRateCommand(ErrorRateCommand errorRateCommand)
    {
        return errorRateCommandMapper.insertErrorRateCommand(errorRateCommand);
    }

    /**
     * 修改误码率命令
     * 
     * @param errorRateCommand 误码率命令
     * @return 结果
     */
    @Override
    public int updateErrorRateCommand(ErrorRateCommand errorRateCommand)
    {
        return errorRateCommandMapper.updateErrorRateCommand(errorRateCommand);
    }

    /**
     * 批量删除误码率命令
     * 
     * @param ids 需要删除的误码率命令主键
     * @return 结果
     */
    @Override
    public int deleteErrorRateCommandByIds(Long[] ids)
    {
        return errorRateCommandMapper.deleteErrorRateCommandByIds(ids);
    }

    /**
     * 删除误码率命令信息
     * 
     * @param id 误码率命令主键
     * @return 结果
     */
    @Override
    public int deleteErrorRateCommandById(Long id)
    {
        return errorRateCommandMapper.deleteErrorRateCommandById(id);
    }

    @Override
    public List<ErrorRateCommand> selectErrorRateCommandListBySQL(ErrorRateCommand errorRateCommand) {

        List<ErrorRateCommand> pojo = new ArrayList<>();
        pojo.addAll(selectErrorRateCommandListByEquivalence(errorRateCommand));

        String equivalence = FunctionalMethods.getEquivalence(errorRateCommand.getBrand());
        if (equivalence!=null){
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
