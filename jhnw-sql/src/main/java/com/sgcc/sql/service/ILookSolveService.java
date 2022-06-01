package com.sgcc.sql.service;

import java.util.List;
import com.sgcc.sql.domain.LookSolve;

/**
 * 前端 回显解决问题命令页面Service接口
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
public interface ILookSolveService 
{
    /**
     * 查询前端 回显解决问题命令页面
     * 
     * @param command 前端 回显解决问题命令页面主键
     * @return 前端 回显解决问题命令页面
     */
    public LookSolve selectLookSolveByCommand(String command);

    /**
     * 查询前端 回显解决问题命令页面列表
     * 
     * @param lookSolve 前端 回显解决问题命令页面
     * @return 前端 回显解决问题命令页面集合
     */
    public List<LookSolve> selectLookSolveList(LookSolve lookSolve);

    /**
     * 新增前端 回显解决问题命令页面
     * 
     * @param lookSolve 前端 回显解决问题命令页面
     * @return 结果
     */
    public int insertLookSolve(LookSolve lookSolve);

    /**
     * 修改前端 回显解决问题命令页面
     * 
     * @param lookSolve 前端 回显解决问题命令页面
     * @return 结果
     */
    public int updateLookSolve(LookSolve lookSolve);

    /**
     * 批量删除前端 回显解决问题命令页面
     * 
     * @param commands 需要删除的前端 回显解决问题命令页面主键集合
     * @return 结果
     */
    public int deleteLookSolveByCommands(String[] commands);

    /**
     * 删除前端 回显解决问题命令页面信息
     * 
     * @param command 前端 回显解决问题命令页面主键
     * @return 结果
     */
    public int deleteLookSolveByCommand(String command);
}
