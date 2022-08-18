package com.sgcc.web.controller.thread;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.sql.domain.SwitchProblem;
import com.sgcc.sql.service.ISwitchProblemService;
import com.sgcc.web.controller.sql.SolveProblemController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年07月29日 15:36
 */
public class RepairFixedThreadPool {

    @Autowired
    private ISwitchProblemService switchProblemService;

    /**
     * newFixedThreadPool submit submit
     */
    public void Solution(LoginUser user, List<Object> userinformation, List<String> problemIdList,int threads) throws InterruptedException {
        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(userinformation.size());

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threads);

        int number = userinformation.size();
        for (int i = 0 ; i<number ; i++){
            // 扫描出问题列表 问题ID
            Long problemId = Integer.valueOf(problemIdList.get(i)).longValue();
            // 根据 问题ID  查询 扫描出的问题
            switchProblemService = SpringBeanUtil.getBean(ISwitchProblemService.class);
            SwitchProblem switchProblem = switchProblemService.selectSwitchProblemById(problemId);
            // 查看 扫描出的问题 是否有问题
            if (switchProblem.getIfQuestion().equals("有问题")){
                // 如果有问题 查询对应交换机登录信息
                String userinformationList = (String) userinformation.get(i);
                // 所有问题
                List<String> problemIds = problemIdList;
                LoginUser loginUser = user;

                fixedThreadPool.submit(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            SolveProblemController solveProblemController = new SolveProblemController();
                            AjaxResult ajaxResult = solveProblemController.batchSolution(userinformationList,loginUser,switchProblem,problemIds);
                            if (ajaxResult.get("msg").equals("交换机基本信息错误，请检查或重新修改")){
                                System.err.println("\r\n交换机基本信息不一致\r\n");
                            }else if (ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析")){
                                System.err.println("\r\n未定义该交换机获取基本信息命令及分析\r\n");
                            }else if (ajaxResult.get("msg").equals("未定义解决问题命令")){
                                System.err.println("\r\n未定义解决问题命令\r\n");
                            }else if (ajaxResult.get("msg").equals("交换机连接失败")){
                                System.err.println("\r\n交换机连接失败\r\n");
                            }else if (ajaxResult.get("msg").equals("修复成功")){
                                System.err.println("\r\n修复成功\r\n");
                            }else if (ajaxResult.get("msg").equals("修复失败")){
                                System.err.println("\r\n修复失败\r\n");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            countDownLatch.countDown();
                        }
                    }
                }));

            }
        }

        countDownLatch.await();
    }
}