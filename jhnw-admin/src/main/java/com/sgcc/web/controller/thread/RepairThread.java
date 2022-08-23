package com.sgcc.web.controller.thread;

import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.sql.domain.SwitchProblem;
import com.sgcc.sql.service.ISwitchProblemService;
import com.sgcc.sql.service.IValueInformationService;
import com.sgcc.web.controller.sql.SolveProblemController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年07月27日 14:07
 */
public class RepairThread extends Thread  {

    @Autowired
    private ISwitchProblemService switchProblemService;

    private static String userinformationList;
    private static Long problemId;
    private static List<String> problemIds;
    private static SwitchProblem switchProblem;
    private static LoginUser loginUser;
    private static Thread myThread;
    @Override
    public void run() {
        Thread thread = myThread;
        SolveProblemController solveProblemController = new SolveProblemController();
        /*String userinformation,LoginUser loginUser, List<SwitchProblem> switchProblems ,List<String> problemIds*/
        //AjaxResult ajaxResult = solveProblemController.batchSolution(userinformationList,loginUser,switchProblem,problemIds);

        thread.interrupt();
    }

    public void Solution(LoginUser user ,List<Object> userinformation,List<String> problemIdList) {
        int number = userinformation.size();
        for (int i = 0 ; i<number ; i++){
            // 扫描出问题列表 问题ID
            problemId = Integer.valueOf(problemIdList.get(i)).longValue();
            // 根据 问题ID  查询 扫描出的问题.
            switchProblemService = SpringBeanUtil.getBean(ISwitchProblemService.class);
            switchProblem = switchProblemService.selectSwitchProblemById(problemId);
            // 查看 扫描出的问题 是否有问题
            if (switchProblem.getIfQuestion().equals("有问题")){
                // 如果有问题 查询对应交换机登录信息
                userinformationList = (String) userinformation.get(i);
                // 所有问题
                problemIds = problemIdList;
                loginUser = user;

                Thread thread = new RepairThread();
                thread.start();
                myThread = thread;
                try {
                    //Thread.sleep(1000*3);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}