package com.sgcc.sql.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.LookSolveMapper;
import com.sgcc.sql.domain.LookSolve;
import com.sgcc.sql.service.ILookSolveService;

/**
 * 前端 回显解决问题命令页面Service业务层处理
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
@Service
public class LookSolveServiceImpl implements ILookSolveService 
{
    @Autowired
    private LookSolveMapper lookSolveMapper;

    /**
     * 查询前端 回显解决问题命令页面
     * 
     * @param command 前端 回显解决问题命令页面主键
     * @return 前端 回显解决问题命令页面
     */
    @Override
    public LookSolve selectLookSolveByCommand(String command)
    {
        return lookSolveMapper.selectLookSolveByCommand(command);
    }

    /**
     * 查询前端 回显解决问题命令页面列表
     * 
     * @param lookSolve 前端 回显解决问题命令页面
     * @return 前端 回显解决问题命令页面
     */
    @Override
    public List<LookSolve> selectLookSolveList(LookSolve lookSolve)
    {
        return lookSolveMapper.selectLookSolveList(lookSolve);
    }

    /**
     * 新增前端 回显解决问题命令页面
     * 
     * @param lookSolve 前端 回显解决问题命令页面
     * @return 结果
     */
    @Override
    public int insertLookSolve(LookSolve lookSolve)
    {
        return lookSolveMapper.insertLookSolve(lookSolve);
    }

    /**
     * 修改前端 回显解决问题命令页面
     * 
     * @param lookSolve 前端 回显解决问题命令页面
     * @return 结果
     */
    @Override
    public int updateLookSolve(LookSolve lookSolve)
    {
        return lookSolveMapper.updateLookSolve(lookSolve);
    }

    /**
     * 批量删除前端 回显解决问题命令页面
     * 
     * @param commands 需要删除的前端 回显解决问题命令页面主键
     * @return 结果
     */
    @Override
    public int deleteLookSolveByCommands(String[] commands)
    {
        return lookSolveMapper.deleteLookSolveByCommands(commands);
    }

    /**
     * 删除前端 回显解决问题命令页面信息
     * 
     * @param command 前端 回显解决问题命令页面主键
     * @return 结果
     */
    @Override
    public int deleteLookSolveByCommand(String command)
    {
        return lookSolveMapper.deleteLookSolveByCommand(command);
    }
}
