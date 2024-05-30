package com.sgcc.advanced.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.sgcc.share.domain.Constant;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.FunctionalMethods;
import com.sgcc.share.util.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.advanced.mapper.OspfCommandMapper;
import com.sgcc.advanced.domain.OspfCommand;
import com.sgcc.advanced.service.IOspfCommandService;

/**
 * OSPF命令Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-05-19
 */
@Service
public class OspfCommandServiceImpl implements IOspfCommandService 
{
    @Autowired
    private OspfCommandMapper ospfCommandMapper;

    /**
     * 查询OSPF命令
     * 
     * @param id OSPF命令主键
     * @return OSPF命令
     */
    @Override
    public OspfCommand selectOspfCommandById(String id)
    {
        return ospfCommandMapper.selectOspfCommandById(id);
    }

    /**
     * 查询OSPF命令列表
     * 
     * @param ospfCommand OSPF命令
     * @return OSPF命令
     */
    @Override
    public List<OspfCommand> selectOspfCommandList(OspfCommand ospfCommand)
    {
        List<OspfCommand> pojo = new ArrayList<>();
        pojo.addAll(ospfCommandMapper.selectOspfCommandList(ospfCommand));

        String equivalence = FunctionalMethods.getEquivalence(ospfCommand.getBrand());
        if (equivalence!=null){
            ospfCommand.setBrand(equivalence);
            pojo.addAll(ospfCommandMapper.selectOspfCommandList(ospfCommand));
        }

        return pojo;
    }

    /**
     * 新增OSPF命令
     * 
     * @param ospfCommand OSPF命令
     * @return 结果
     */
    @Override
    public int insertOspfCommand(OspfCommand ospfCommand)
    {
        String regionalCode = (CustomConfigurationUtil.getValue("configuration.problemCode.日常巡检", Constant.getProfileInformation())).toString();
        String id = MyUtils.getID(regionalCode, null);

        ospfCommand.setId(id);

        return ospfCommandMapper.insertOspfCommand(ospfCommand);
    }

    /**
     * 修改OSPF命令
     * 
     * @param ospfCommand OSPF命令
     * @return 结果
     */
    @Override
    public int updateOspfCommand(OspfCommand ospfCommand)
    {
        return ospfCommandMapper.updateOspfCommand(ospfCommand);
    }

    /**
     * 批量删除OSPF命令
     * 
     * @param ids 需要删除的OSPF命令主键
     * @return 结果
     */
    @Override
    public int deleteOspfCommandByIds(String[] ids)
    {
        return ospfCommandMapper.deleteOspfCommandByIds(ids);
    }

    /**
     * 删除OSPF命令信息
     * 
     * @param id OSPF命令主键
     * @return 结果
     */
    @Override
    public int deleteOspfCommandById(String id)
    {
        return ospfCommandMapper.deleteOspfCommandById(id);
    }

    @Override
    public List<OspfCommand> selectOspfCommandListBySQL(OspfCommand ospfCommand) {
        List<OspfCommand> pojo = new ArrayList<>();
        pojo.addAll(selectOspfCommandListByEquivalence(ospfCommand));

        String equivalence = FunctionalMethods.getEquivalence(ospfCommand.getBrand());
        if (equivalence!=null){
            ospfCommand.setBrand(equivalence);
            pojo.addAll(selectOspfCommandListByEquivalence(ospfCommand));
        }

        return pojo;
    }



    public List<OspfCommand> selectOspfCommandListByEquivalence(OspfCommand ospfCommand) {
        String fuzzySQL = FunctionalMethods.getFuzzySQL(ospfCommand.getBrand(), ospfCommand.getSwitchType(),
                ospfCommand.getFirewareVersion(), ospfCommand.getSubVersion());

        return ospfCommandMapper.selectOspfCommandListBySQL(fuzzySQL);
    }
}
