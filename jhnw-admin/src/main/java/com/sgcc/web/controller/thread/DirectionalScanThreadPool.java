package com.sgcc.web.controller.thread;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.web.controller.sql.SwitchInteraction;
import com.sgcc.web.controller.webSocket.WebSocketService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年08月09日 14:04
 */
public class DirectionalScanThreadPool {

    public static HashMap<String,String> ip_information = new HashMap<>();

    /**
     * newFixedThreadPool submit submit
     */
    public static void switchLoginInformations(List<Object[]> objects, List<TotalQuestionTable> totalQuestionTables, String ScanningTime, LoginUser login, int threads) throws InterruptedException {

        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(objects.size());

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threads);

        for (Object[] objects3:objects){

            String mode = (String)objects3[0];
            String ip = (String)objects3[1];
            String name = (String)objects3[2];
            String password = (String)objects3[3];
            String configureCiphers = (String) objects3[4];
            int port = (int) objects3[5];
            LoginUser loginUser = login;
            String time = ScanningTime;

            fixedThreadPool.submit(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        SwitchInteraction switchInteraction = new SwitchInteraction();
                        String userName = loginUser.getUsername();
                        //扫描方法 logInToGetBasicInformation  传参 ：mode连接方式, ip 地址, name 用户名, password 密码, port 端口号
                        AjaxResult ajaxResult = switchInteraction.logInToGetBasicInformation(mode, ip, name, password, configureCiphers , port, loginUser,time, totalQuestionTables);

                        if (ajaxResult.get("msg").equals("交换机连接失败")){
                            WebSocketService.sendMessage("error"+userName,"\r\n"+ip + ":交换机连接失败\r\n");
                        }else if (ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析")){
                            WebSocketService.sendMessage("error"+userName,"\r\n"+ip + ":未定义该交换机获取基本信息命令及分析\r\n");
                        }
                        //ip_information.put(ip+loginUser.getUsername());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            }));
        }
        countDownLatch.await();
    }

}