package com.sgcc.sql.service.impl;

import com.sgcc.share.domain.ValueInformationVO;
import com.sgcc.sql.domain.ValueInformation;
import com.sgcc.sql.mapper.ValueInformationMapper;
import com.sgcc.sql.service.IValueInformationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 取值信息存储Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-12-28
 */
@Service
public class ValueInformationServiceImpl implements IValueInformationService 
{
    @Autowired
    private ValueInformationMapper valueInformationMapper;

    /**
     * 查询取值信息存储
     * 
     * @param id 取值信息存储主键
     * @return 取值信息存储
     */
    @Override
    public ValueInformation selectValueInformationById(Long id)
    {
        return valueInformationMapper.selectValueInformationById(id);
    }

    /**
     * 查询取值信息存储列表
     * 
     * @param valueInformation 取值信息存储
     * @return 取值信息存储
     */
    @Override
    public List<ValueInformation> selectValueInformationList(ValueInformation valueInformation)
    {
        return valueInformationMapper.selectValueInformationList(valueInformation);
    }

    /**
     * 新增取值信息存储
     * 
     * @param valueInformation 取值信息存储
     * @return 结果
     */
    @Override
    public int insertValueInformation(ValueInformation valueInformation)
    {

        return valueInformationMapper.insertValueInformation(valueInformation);
    }

    /**
     * 修改取值信息存储
     * 
     * @param valueInformation 取值信息存储
     * @return 结果
     */
    @Override
    public int updateValueInformation(ValueInformation valueInformation)
    {
        return valueInformationMapper.updateValueInformation(valueInformation);
    }

    /**
     * 批量删除取值信息存储
     * 
     * @param ids 需要删除的取值信息存储主键
     * @return 结果
     */
    @Override
    public int deleteValueInformationByIds(Long[] ids)
    {
        return valueInformationMapper.deleteValueInformationByIds(ids);
    }

    /**
     * 删除取值信息存储信息
     * 
     * @param id 取值信息存储主键
     * @return 结果
     */
    @Override
    public int deleteValueInformationById(Long id)
    {
        return valueInformationMapper.deleteValueInformationById(id);
    }

    /**
    * @method: 根据第一个参数id 查询参数列表
    * @Param: [id]
    * @return: java.util.List<com.sgcc.sql.domain.ValueInformationVO>
    */
    @Override
    public List<ValueInformationVO> selectValueInformationVOListByID(Long id) {
        List<ValueInformation> valueInformationList = new ArrayList<>();
        while (id!=0L){
            ValueInformation valueInformation = valueInformationMapper.selectValueInformationById(id);
            valueInformationList.add(valueInformation);
            id = valueInformation.getOutId();
        }
        List<ValueInformationVO> valueInformationVOList = new ArrayList<>();
        for (ValueInformation valueInformation:valueInformationList){
            ValueInformationVO valueInformationVO = new ValueInformationVO();
            BeanUtils.copyProperties(valueInformation,valueInformationVO);
            valueInformationVO.setParameterId(valueInformation.getId());
            valueInformationVOList.add(valueInformationVO);
        }

        return valueInformationVOList;
    }

    @Override
    public List<ValueInformation> selectValueInformationListByID(Long valueId) {

        List<ValueInformation> valueInformationList = new ArrayList<>();
        while (valueId!=0L){
            ValueInformation valueInformation = valueInformationMapper.selectValueInformationById(valueId);
            valueInformationList.add(valueInformation);
            valueId = valueInformation.getOutId();
        }

        return valueInformationList;
    }
}
