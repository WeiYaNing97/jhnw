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