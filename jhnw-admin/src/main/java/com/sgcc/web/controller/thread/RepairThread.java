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
        AjaxResult ajaxResult = solveProblemController.batchSolution(userinformationList,loginUser,switchProblem,problemIds);

        thread.interrupt();
    }

    public void Solution(LoginUser user,String userinformation,Long id,List<String> problemIdList) {

        problemId = id;
        switchProblemService = SpringBeanUtil.getBean(ISwitchProblemService.class);
        switchProblem = switchProblemService.selectSwitchProblemById(problemId);

        if (switchProblem.getIfQuestion().equals("有问题")){

            userinformationList = userinformation;

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